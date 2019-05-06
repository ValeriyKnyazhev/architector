package valeriy.knyazhev.architector.application.project.file;

import lombok.RequiredArgsConstructor;
import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.command.*;
import valeriy.knyazhev.architector.domain.model.commit.*;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

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
            command.author(),
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
            command.author(),
            "File from uploaded file was added to project.",
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
            command.author(),
            "File " + command.fileId().id() + " was updated from uploaded file.",
            newFile);
    }

    public boolean updateFileMetadata(@Nonnull UpdateFileMetadataCommand command)
    {
        Args.notNull(command, "Update file metadata command is required.");
        ProjectId projectId = command.projectId();
        Project project = findProject(projectId);
        FileId fileId = command.fileId();
        File foundFile = project.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId));
        FileMetadata newMetadata = command.constructMetadata();
        FileMetadataChanges changes = FileDiffCalculator.defineMetadataChanges(
            foundFile.metadata(), newMetadata
        );
        if (changes.isEmpty())
        {
            return false;
        } else
        {
            updateFile(projectId, fileId, command.author(),
                "File " + fileId.id() + " metadata was updated.",
                new FileData("FIXME ISO_ID", newMetadata, foundFile.description(), foundFile.content())
            );
            foundFile.updateMetadata(newMetadata);
            return true;
        }
    }

    public boolean updateFileDescription(@Nonnull UpdateFileDescriptionCommand command)
    {
        Args.notNull(command, "Update file description command is required.");
        ProjectId projectId = command.projectId();
        Project project = findProject(projectId);
        FileId fileId = command.fileId();
        File foundFile = project.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId));
        FileDescription newDescription = command.constructDescription();
        FileDescriptionChanges changes = FileDiffCalculator.defineDescriptionChanges(
            foundFile.description(), newDescription
        );
        if (changes.isEmpty())
        {
            return false;
        } else
        {
            updateFile(projectId, fileId, command.author(),
                "File " + fileId.id() + " description was updated.",
                new FileData("FIXME ISO_ID", foundFile.metadata(), newDescription, foundFile.content())
            );
            foundFile.updateDescription(newDescription);
            return true;
        }
    }

    public boolean deleteFile(@Nonnull DeleteFileCommand command)
    {
        Args.notNull(command, "Update file from upload file command is required.");
        return deleteFile(command.projectId(), command.fileId(), command.author());
    }

    @Nullable
    private File addNewFile(@Nonnull ProjectId projectId,
                            @Nonnull String author,
                            @Nonnull String commitMessage,
                            @Nonnull FileData newFileData)
    {
        Project project = findProject(projectId);
        File newFile = constructFile(FileId.nextId(), newFileData);
        project.addFile(newFile);
        projectRepository.save(project);
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
        boolean added = commitChanges(
            project.projectId(),
            author,
            commitMessage,
            commitData);
        return added ? newFile : null;
    }

    private boolean updateFile(@Nonnull ProjectId projectId,
                               @Nonnull FileId fileId,
                               @Nonnull String author,
                               @Nonnull String commitMessage,
                               @Nonnull FileData newFileData)
    {
        Project project = findProject(projectId);
        File oldFile = project.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId));
        File newFile = constructFile(fileId, newFileData);
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
        return commitChanges(project.projectId(), author, commitMessage, commitData);
    }

    private boolean deleteFile(@Nonnull ProjectId projectId,
                               @Nonnull FileId fileId,
                               @Nonnull String author)
    {
        Project project = findProject(projectId);
        File deleted = project.deleteFile(fileId);
        this.projectRepository.save(project);
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
        return commitChanges(
            project.projectId(),
            author,
            "File " + fileId.id() + " was deleted from project.",
            commitData
        );
    }

    private void updateFileContent(@Nonnull Project project,
                                   @Nonnull FileId fileId,
                                   @Nonnull File newFile)
    {
        project.updateFile(fileId, newFile.content());
        this.projectRepository.save(project);
    }

    @Nonnull
    private Project findProject(@Nonnull ProjectId projectId)
    {
        return this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }

    // TODO move to commit service
    private boolean commitChanges(@Nonnull ProjectId projectId,
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
        this.commitRepository.save(newCommit);
        return true;
    }

    @Nonnull
    private static File constructFile(@Nonnull FileId fileId,
                                      @Nonnull FileData fileData)
    {
        return File.constructor()
            .withFileId(fileId)
            .withDescription(fileData.description())
            .withMetadata(fileData.metadata())
            .withContent(fileData.content())
            .construct();
    }
}

