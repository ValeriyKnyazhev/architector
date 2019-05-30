package valeriy.knyazhev.architector.application.project.file;

import lombok.RequiredArgsConstructor;
import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.command.*;
import valeriy.knyazhev.architector.domain.model.AccessRightsNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.*;
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

import static java.util.Collections.singletonList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RequiredArgsConstructor
@Service
@Transactional
public class FileManagementService
{

    private final ProjectRepository projectRepository;

    private final IFCFileReader fileReader;

    private final CommitRepository commitRepository;

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

    public boolean updateFile(@Nonnull UpdateFileContentCommand command)
    {
        Args.notNull(command, "Update file from url command is required.");
        ProjectId projectId = command.projectId();
        Architector architector = command.architector();
        FileId fileId = command.fileId();
        File foundFile = fetchFile(projectId, fileId, architector);
        FileData newFile = new FileData(
            foundFile.schema(),
            "FIXME ISO_ID",
            foundFile.metadata(),
            foundFile.description(),
            FileContent.of(command.content())
        );
        return updateFile(
            command.projectId(),
            command.fileId(),
            architector,
            "File " + command.fileId().id() + " content was updated: " + command.commitMessage(),
            newFile);
    }

    public boolean updateFile(@Nonnull UpdateFileFromUrlCommand command)
    {
        Args.notNull(command, "Update file from url command is required.");
        FileData newFile = null;
        try
        {
            URL sourceUrl = new URL(command.sourceUrl());
            newFile = this.fileReader.readFromUrl(sourceUrl);
        } catch (MalformedURLException e)
        {
            return false;
        }
        return updateFile(
            command.projectId(),
            command.fileId(),
            command.author(),
            "File " + command.fileId().id() + " was updated from " + command.sourceUrl() + ".",
            newFile);
    }

    public boolean updateFile(@Nonnull UpdateFileFromUploadCommand command)
    {
        Args.notNull(command, "Update file from upload file command is required.");
        FileData newFile = null;
        try
        {
            MultipartFile multipartFile = command.content();
            newFile = this.fileReader.readFromFile(multipartFile.getInputStream());
        } catch (IOException ex)
        {
            return false;
        }
        return updateFile(
            command.projectId(),
            command.fileId(),
            command.architector(),
            "File " + command.fileId().id() + " was updated from uploaded file.",
            newFile);
    }

    public boolean updateFileMetadata(@Nonnull UpdateFileMetadataCommand command)
    {
        Args.notNull(command, "Update file metadata command is required.");
        ProjectId projectId = command.projectId();
        Architector architector = command.architector();
        FileId fileId = command.fileId();
        File foundFile = fetchFile(projectId, fileId, architector);
        FileMetadata newMetadata = command.constructMetadata();
        FileMetadataChanges changes = FileDiffCalculator.defineMetadataChanges(
            foundFile.metadata(), newMetadata
        );
        if (changes.isEmpty())
        {
            return false;
        } else
        {
            updateFile(
                projectId,
                fileId,
                architector,
                "File " + fileId.id() + " metadata was updated.",
                new FileData(
                    foundFile.schema(), "FIXME ISO_ID", newMetadata, foundFile.description(), foundFile.content()
                )
            );
            foundFile.updateMetadata(newMetadata);
            return true;
        }
    }

    public boolean updateFileDescription(@Nonnull UpdateFileDescriptionCommand command)
    {
        Args.notNull(command, "Update file description command is required.");
        ProjectId projectId = command.projectId();
        Architector architector = command.architector();
        FileId fileId = command.fileId();
        File foundFile = fetchFile(projectId, fileId, architector);
        FileDescription newDescription = command.constructDescription();
        FileDescriptionChanges changes = FileDiffCalculator.defineDescriptionChanges(
            foundFile.description(), newDescription
        );
        if (changes.isEmpty())
        {
            return false;
        } else
        {
            updateFile(
                projectId,
                fileId,
                architector,
                "File " + fileId.id() + " description was updated.",
                new FileData(
                    foundFile.schema(), "FIXME ISO_ID", foundFile.metadata(), newDescription, foundFile.content()
                )
            );
            foundFile.updateDescription(newDescription);
            return true;
        }
    }

    public boolean deleteFile(@Nonnull DeleteFileCommand command)
    {
        Args.notNull(command, "Update file from upload file command is required.");
        return deleteFile(command.projectId(), command.fileId(), command.architector());
    }

    @Nullable
    private File addNewFile(@Nonnull ProjectId projectId,
                            @Nonnull Architector architector,
                            @Nonnull String commitMessage,
                            @Nonnull FileData newFileData)
    {
        Project project = findProject(projectId, architector);
        File newFile = constructFile(FileId.nextId(), newFileData);
        project.addFile(newFile);
        this.projectRepository.saveAndFlush(project);
        CommitDescription commitData = CommitDescription.builder()
            .files(
                singletonList(
                    CommitFileItem.of(
                        newFile.fileId(),
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
            architector.email(),
            commitMessage,
            commitData);
        if (commitId == null)
        {
            return null;
        }
        project.updateCurrentCommitId(commitId);
        return newFile;
    }

    private boolean updateFile(@Nonnull ProjectId projectId,
                               @Nonnull FileId fileId,
                               @Nonnull Architector architector,
                               @Nonnull String commitMessage,
                               @Nonnull FileData newFileData)
    {
        Project project = findProject(projectId, architector);
        File oldFile = project.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId));
        File newFile = constructFile(fileId, newFileData);
        if (!oldFile.schema().equals(newFile.schema()))
        {
            throw new IllegalStateException("File schema version must not be updated.");
        }
        List<CommitItem> commitItems = FileDiffCalculator.calculateDiff(
            oldFile.content(), newFile.content()
        );
        FileMetadataChanges fileMetadataChanges = FileDiffCalculator.defineMetadataChanges(
            oldFile.metadata(), newFile.metadata()
        );
        FileDescriptionChanges fileDescriptionChanges = FileDiffCalculator.defineDescriptionChanges(
            oldFile.description(), newFile.description()
        );
        CommitDescription commitData = CommitDescription.builder()
            .files(
                singletonList(
                    CommitFileItem.of(
                        oldFile.fileId(),
                        fileMetadataChanges,
                        fileDescriptionChanges,
                        commitItems
                    )
                )
            )
            .build();
        updateFileContent(project, oldFile.fileId(), newFile);
        Long commitId = commitChanges(project.projectId(), architector.email(), commitMessage, commitData);
        if (commitId == null)
        {
            return false;
        }
        project.updateCurrentCommitId(commitId);
        return true;
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
            architector.email(),
            "File " + fileId.id() + " was deleted from project.",
            commitData
        );
        if (commitId == null)
        {
            return false;
        }
        project.updateCurrentCommitId(commitId);
        return true;
    }

    private void updateFileContent(@Nonnull Project project,
                                   @Nonnull FileId fileId,
                                   @Nonnull File newFile)
    {
        project.updateFile(fileId, newFile.content());
        this.projectRepository.saveAndFlush(project);
    }

    @Nonnull
    private Project findProject(@Nonnull ProjectId projectId, @Nonnull Architector architector)
    {
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        if (!project.canBeUpdated(architector))
        {
            throw new AccessRightsNotFoundException();
        }
        else {
            return project;
        }
    }

    @Nonnull
    private File fetchFile(@Nonnull ProjectId projectId, @Nonnull FileId fileId, @Nonnull Architector architector)
    {
        Project project = findProject(projectId, architector);
        return project.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId));
    }

    // TODO move to commit service
    @Nullable
    private Long commitChanges(@Nonnull ProjectId projectId,
                                  @Nonnull String author,
                                  @Nonnull String commitMessage,
                                  @Nonnull CommitDescription commitData)
    {
        // FIXME get next id from db
        Long parentId = this.commitRepository.findByProjectIdOrderById(projectId)
            .stream()
            .map(Commit::id)
            .max(Long::compareTo)
            .orElse(null);
        Commit newCommit = Commit.builder()
            .parentId(parentId)
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
            .withSchema(fileData.schema())
            .withDescription(fileData.description())
            .withMetadata(fileData.metadata())
            .withContent(fileData.content())
            .construct();
    }
}

