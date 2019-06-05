package valeriy.knyazhev.architector.application.project.file;

import lombok.RequiredArgsConstructor;
import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.application.commit.ProjectionConstructService;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.command.*;
import valeriy.knyazhev.architector.application.project.file.conflict.*;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictChanges;
import valeriy.knyazhev.architector.application.project.file.conflict.data.DescriptionConflictChanges;
import valeriy.knyazhev.architector.application.project.file.conflict.data.MetadataConflictChanges;
import valeriy.knyazhev.architector.application.project.file.conflict.exception.FileContentConflictException;
import valeriy.knyazhev.architector.application.project.file.conflict.exception.FileDescriptionConflictException;
import valeriy.knyazhev.architector.application.project.file.conflict.exception.FileMetadataConflictException;
import valeriy.knyazhev.architector.domain.model.AccessRightsNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.*;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection.FileProjection;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.*;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RequiredArgsConstructor
@Service
@Transactional
public class FileManagementService
{

    private final ProjectRepository projectRepository;

    private final ProjectionConstructService projectionConstructService;

    private final IFCFileReader fileReader;

    private final CommitRepository commitRepository;

    private final ResolveChangesConflictService conflictService;

    @Nullable
    public File addFile(@Nonnull AddFileFromUrlCommand command)
    {
        Args.notNull(command, "Add file from url command is required.");
        FileData newFile = null;
        try
        {
            URL sourceUrl = new URL(command.sourceUrl());
            newFile = this.fileReader.readFromUrl(sourceUrl);
        } catch (MalformedURLException e)
        {
            return null;
        }
        return addNewFile(
            command.projectId(),
            command.architector(),
            "File from " + command.sourceUrl() + " was added to project.",
            newFile);
    }

    @Nullable
    public File addFile(@Nonnull AddFileFromUploadCommand command)
    {
        Args.notNull(command, "Add file from upload file command is required.");
        FileData newFile = null;
        try
        {
            MultipartFile multipartFile = command.content();
            newFile = this.fileReader.readFromFile(multipartFile.getInputStream());
        } catch (IOException ex)
        {
            return null;
        }
        return addNewFile(
            command.projectId(),
            command.architector(),
            "File from uploaded file was added to project.",
            newFile);
    }

    public boolean updateFileContent(@Nonnull UpdateFileContentCommand command)
        throws FileContentConflictException
    {
        Args.notNull(command, "Update file content command is required.");
        ProjectId projectId = command.projectId();
        Architector architector = command.architector();
        FileId fileId = command.fileId();
        Project project = findProject(projectId, architector);
        File foundFile = findFile(project, fileId);
        Long projectCommitId = project.currentCommitId();
        if (projectCommitId == null)
        {
            throw new IllegalStateException("Project must have some changes.");
        }
        FileContent newContent = FileContent.of(command.content());
        CommitDescription commitData = null;
        if (projectCommitId == command.headCommitId())
        {
            List<CommitItem> commitItems = FileDiffCalculator.calculateDiff(
                foundFile.content(), newContent
            );
            commitData = CommitDescription.builder()
                .files(
                    singletonList(
                        CommitFileItem.of(
                            fileId,
                            foundFile.isoId(),
                            foundFile.schema(),
                            FileMetadataChanges.empty(),
                            FileDescriptionChanges.empty(),
                            commitItems
                        )
                    )
                )
                .build();
        } else
        {
            // FIXME get projection by commit id and calc diff
            FileProjection projection = fetchFileProjection(projectId, fileId, command.headCommitId());
            FileContent oldContent = FileContent.of(projection.items());
            List<CommitItem> headCommitItems = FileDiffCalculator.calculateDiff(oldContent, foundFile.content());
            List<CommitItem> newCommitItems = FileDiffCalculator.calculateDiff(oldContent, newContent);
            ContentConflictChanges conflicts = this.conflictService.checkContentChangesConflicts(
                oldContent.items(), headCommitItems, newCommitItems
            );
            if (conflicts.isEmpty())
            {
                List<CommitItem> mergedCommitItems = Stream.concat(headCommitItems.stream(), newCommitItems.stream())
                    .sorted(CommitItem::compareTo)
                    .collect(toList());
                List<String> mergedContent = ContentMerger.applyChanges(oldContent.items(), mergedCommitItems);
                List<CommitItem> diffItems = FileDiffCalculator
                    .calculateDiff(foundFile.content(), FileContent.of(mergedContent));
                commitData = CommitDescription.builder()
                    .files(
                        singletonList(
                            CommitFileItem.of(
                                fileId,
                                projection.isoId(),
                                projection.schema(),
                                FileMetadataChanges.empty(),
                                FileDescriptionChanges.empty(),
                                diffItems
                            )
                        )
                    )
                    .build();
            } else
            {
                throw new FileContentConflictException(
                    oldContent.items(), conflicts.headBlocks(), conflicts.newBlocks(), projectCommitId
                );
            }
        }
        Long commitId = commitChanges(
            project.projectId(),
            projectCommitId,
            architector.email(),
            command.commitMessage(),
            commitData
        );
        project.updateFile(fileId, newContent);
        project.updateCurrentCommitId(commitId);
        this.projectRepository.saveAndFlush(project);
        return true;
    }

    public boolean updateFileMetadata(@Nonnull UpdateFileMetadataCommand command)
        throws FileMetadataConflictException
    {
        Args.notNull(command, "Update file metadata command is required.");
        ProjectId projectId = command.projectId();
        Architector architector = command.architector();
        FileId fileId = command.fileId();
        Project project = findProject(projectId, architector);
        File foundFile = findFile(project, fileId);
        Long projectCommitId = project.currentCommitId();
        if (projectCommitId == null)
        {
            throw new IllegalStateException("Project must have some changes.");
        }
        FileMetadata newMetadata = command.constructMetadata();
        CommitDescription commitData = null;
        if (projectCommitId == command.headCommitId())
        {
            FileMetadataChanges newChanges = FileDiffCalculator.defineMetadataChanges(
                foundFile.metadata(), newMetadata
            );
            commitData = CommitDescription.builder()
                .files(
                    singletonList(
                        CommitFileItem.of(
                            fileId,
                            foundFile.isoId(),
                            foundFile.schema(),
                            newChanges,
                            FileDescriptionChanges.empty(),
                            List.of()
                        )
                    )
                )
                .build();
        } else
        {
            // FIXME get projection by commit id and calc diff
            FileProjection projection = fetchFileProjection(projectId, fileId, command.headCommitId());
            FileMetadata oldMetadata = projection.metadata();
            FileMetadataChanges headChanges = FileDiffCalculator.defineMetadataChanges(
                oldMetadata, foundFile.metadata()
            );
            FileMetadataChanges newChanges = FileDiffCalculator.defineMetadataChanges(
                oldMetadata, newMetadata
            );
            MetadataConflictChanges conflicts = this.conflictService.checkMetadataChangesConflicts(
                oldMetadata, headChanges, newChanges
            );
            if (conflicts.isEmpty())
            {
                commitData = CommitDescription.builder()
                    .files(
                        singletonList(
                            CommitFileItem.of(
                                fileId,
                                projection.isoId(),
                                projection.schema(),
                                newChanges,
                                FileDescriptionChanges.empty(),
                                List.of()
                            )
                        )
                    )
                    .build();
            } else
            {
                throw new FileMetadataConflictException(conflicts, projectCommitId);
            }
        }
        Long commitId = commitChanges(
            project.projectId(),
            projectCommitId,
            architector.email(),
            "File " + fileId.id() + " metadata was updated.",
            commitData
        );
        foundFile.updateMetadata(newMetadata);
        project.updateCurrentCommitId(commitId);
        this.projectRepository.saveAndFlush(project);
        return true;
    }

    public boolean updateFileDescription(@Nonnull UpdateFileDescriptionCommand command)
        throws FileDescriptionConflictException
    {
        Args.notNull(command, "Update file description command is required.");
        ProjectId projectId = command.projectId();
        Architector architector = command.architector();
        FileId fileId = command.fileId();
        Project project = findProject(projectId, architector);
        File foundFile = findFile(project, fileId);
        Long projectCommitId = project.currentCommitId();
        if (projectCommitId == null)
        {
            throw new IllegalStateException("Project must have some changes.");
        }
        FileDescription newDescription = command.constructDescription();
        CommitDescription commitData = null;
        if (projectCommitId == command.headCommitId())
        {
            FileDescriptionChanges newChanges = FileDiffCalculator.defineDescriptionChanges(
                foundFile.description(), newDescription
            );
            commitData = CommitDescription.builder()
                .files(
                    singletonList(
                        CommitFileItem.of(
                            fileId,
                            foundFile.isoId(),
                            foundFile.schema(),
                            FileMetadataChanges.empty(),
                            newChanges,
                            List.of()
                        )
                    )
                )
                .build();
        } else
        {
            // FIXME get projection by commit id and calc diff
            FileProjection projection = fetchFileProjection(projectId, fileId, command.headCommitId());
            FileDescription oldDescription = projection.description();
            FileDescriptionChanges newChanges = FileDiffCalculator.defineDescriptionChanges(
                oldDescription, newDescription
            );
            FileDescriptionChanges headChanges = FileDiffCalculator.defineDescriptionChanges(
                oldDescription, foundFile.description()
            );
            DescriptionConflictChanges conflicts = this.conflictService.checkDescriptionChangesConflicts(
                oldDescription, headChanges, newChanges
            );
            if (conflicts.isEmpty())
            {
                commitData = CommitDescription.builder()
                    .files(
                        singletonList(
                            CommitFileItem.of(
                                fileId,
                                projection.isoId(),
                                projection.schema(),
                                FileMetadataChanges.empty(),
                                newChanges,
                                List.of()
                            )
                        )
                    )
                    .build();
            } else
            {
                throw new FileDescriptionConflictException(conflicts, projectCommitId);
            }
        }
        Long commitId = commitChanges(
            project.projectId(),
            projectCommitId,
            architector.email(),
            "File " + fileId.id() + " description was updated.",
            commitData
        );
        foundFile.updateDescription(newDescription);
        project.updateCurrentCommitId(commitId);
        this.projectRepository.saveAndFlush(project);
        return true;
    }

    public boolean deleteFile(@Nonnull DeleteFileCommand command)
    {
        Args.notNull(command, "Update file from upload file command is required.");
        return deleteFile(command.projectId(), command.fileId(), command.architector());
    }

    @Nonnull
    private File addNewFile(@Nonnull ProjectId projectId,
                            @Nonnull Architector architector,
                            @Nonnull String commitMessage,
                            @Nonnull FileData newFileData)
    {
        Project project = findProject(projectId, architector);
        File newFile = constructFile(FileId.nextId(), newFileData);
        CommitDescription commitData = CommitDescription.builder()
            .files(
                singletonList(
                    CommitFileItem.of(
                        newFile.fileId(),
                        newFile.isoId(),
                        newFile.schema(),
                        FileDiffCalculator.defineMetadataChanges(null, newFile.metadata()),
                        FileDiffCalculator.defineDescriptionChanges(null, newFile.description()),
                        FileDiffCalculator.calculateDiff(
                            null, newFile.content()
                        )
                    )
                )
            )
            .build();
        Long commitId = commitChanges(
            project.projectId(),
            project.currentCommitId(),
            architector.email(),
            commitMessage,
            commitData
        );
        project.addFile(newFile);
        this.projectRepository.saveAndFlush(project);
        project.updateCurrentCommitId(commitId);
        return newFile;
    }

    private boolean deleteFile(@Nonnull ProjectId projectId,
                               @Nonnull FileId fileId,
                               @Nonnull Architector architector)
    {
        Project project = findProject(projectId, architector);
        File deleted = project.deleteFile(fileId);
        this.projectRepository.saveAndFlush(project);
        CommitDescription commitData = CommitDescription.builder()
            .files(
                singletonList(
                    CommitFileItem.of(
                        deleted.fileId(),
                        deleted.isoId(),
                        deleted.schema(),
                        FileDiffCalculator.defineMetadataChanges(deleted.metadata(), null),
                        FileDiffCalculator.defineDescriptionChanges(deleted.description(), null),
                        FileDiffCalculator.calculateDiff(
                            deleted.content(), null
                        )
                    )
                )
            )
            .build();
        Long commitId = commitChanges(
            project.projectId(),
            project.currentCommitId(),
            architector.email(),
            "File " + fileId.id() + " was deleted from project.",
            commitData
        );
        project.updateCurrentCommitId(commitId);
        return true;
    }

    @Nonnull
    private Project findProject(@Nonnull ProjectId projectId, @Nonnull Architector architector)
    {
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        if (!project.canBeUpdated(architector))
        {
            throw new AccessRightsNotFoundException();
        } else
        {
            return project;
        }
    }

    @Nonnull
    private File findFile(@Nonnull Project project, @Nonnull FileId fileId)
    {
        return project.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(project.projectId(), fileId));
    }

    @Nonnull
    private FileProjection fetchFileProjection(@Nonnull ProjectId projectId,
                                               @Nonnull FileId fileId,
                                               @Nullable Long commitId)
    {
        Projection projection = projectionConstructService.makeProjection(projectId, commitId);
        return projection.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId));
    }

    // TODO move to commit service
    @Nonnull
    private Long commitChanges(@Nonnull ProjectId projectId,
                               @Nullable Long headCommitId,
                               @Nonnull String author,
                               @Nonnull String commitMessage,
                               @Nonnull CommitDescription commitData)
    {
        Commit newCommit = Commit.builder()
            .parentId(headCommitId)
            .projectId(projectId)
            .message(commitMessage)
            .author(author)
            .data(commitData)
            .build();
        this.commitRepository.saveAndFlush(newCommit);
        return newCommit.id();
    }

    @Nonnull
    private static File constructFile(@Nonnull FileId fileId,
                                      @Nonnull FileData fileData)
    {
        return File.constructor()
            .withFileId(fileId)
            .withIsoId(fileData.isoId())
            .withSchema(fileData.schema())
            .withDescription(fileData.description())
            .withMetadata(fileData.metadata())
            .withContent(fileData.content())
            .construct();
    }
}

