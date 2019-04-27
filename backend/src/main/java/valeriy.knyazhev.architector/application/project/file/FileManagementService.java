package valeriy.knyazhev.architector.application.project.file;

import lombok.RequiredArgsConstructor;
import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.command.AddFileFromUploadCommand;
import valeriy.knyazhev.architector.application.project.file.command.AddFileFromUrlCommand;
import valeriy.knyazhev.architector.application.project.file.command.UpdateFileFromUploadCommand;
import valeriy.knyazhev.architector.application.project.file.command.UpdateFileFromUrlCommand;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.commit.*;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

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
public class FileManagementService {

    private final ProjectRepository projectRepository;

    private final IFCFileReader fileReader;

    private final FileDiffCalculator diffCalculator;

    private final CommitRepository commitRepository;

    @Nullable
    public File addFile(@Nonnull AddFileFromUrlCommand command) {
        Args.notNull(command, "Add file from url command is required.");
        File newFile = null;
        try {
            URL sourceUrl = new URL(command.sourceUrl());
            newFile = this.fileReader.readFromUrl(sourceUrl);
        } catch (MalformedURLException e) {
            return null;
        }
        return addFile(command.projectId(), command.author(), newFile);
    }

    @Nullable
    public File addFile(@Nonnull AddFileFromUploadCommand command) {
        Args.notNull(command, "Add file from upload file command is required.");
        File newFile = null;
        try {
            MultipartFile multipartFile = command.content();
            newFile = this.fileReader.readFromFile(multipartFile.getInputStream());
        } catch (IOException ex) {
            return null;
        }
        return addFile(command.projectId(), command.author(), newFile);
    }

    public boolean updateFile(@Nonnull UpdateFileFromUrlCommand command) {
        Args.notNull(command, "Update file from url command is required.");
        File newFile = null;
        try {
            URL sourceUrl = new URL(command.sourceUrl());
            newFile = this.fileReader.readFromUrl(sourceUrl);
        } catch (MalformedURLException e) {
            return false;
        }
        return updateFile(command.projectId(), command.fileId(), command.author(), newFile);
    }

    public boolean updateFile(@Nonnull UpdateFileFromUploadCommand command) {
        Args.notNull(command, "Update file from upload file command is required.");
        File newFile = null;
        try {
            MultipartFile multipartFile = command.content();
            newFile = this.fileReader.readFromFile(multipartFile.getInputStream());
        } catch (IOException ex) {
            return false;
        }
        return updateFile(command.projectId(), command.fileId(), command.author(), newFile);
    }

    @Nullable
    private File addFile(@Nonnull ProjectId projectId,
                         @Nonnull String author,
                         @Nonnull File newFile) {
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        project.addFile(newFile);
        projectRepository.save(project);
        CommitDescription commitData = CommitDescription.of(
            singletonList(
                CommitFileItem.of(
                    newFile.fileId(),
                    this.diffCalculator.calculateDiff(null, newFile)
                )
            )
        );
        return commitChanges(project.projectId(), author, commitData)
            ? newFile
            : null;
    }

    private boolean updateFile(@Nonnull ProjectId projectId,
                               @Nonnull FileId fileId,
                               @Nonnull String author,
                               @Nonnull File newFile) {
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        File oldFile = project.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId));
        List<CommitItem> commitItems = this.diffCalculator.calculateDiff(oldFile, newFile);
        if (commitItems.isEmpty()) {
            // TODO return answer that nothing to commit
            return false;
        }
        updateFileContent(project, oldFile.fileId(), newFile);
        CommitDescription commitData = CommitDescription.of(
            singletonList(CommitFileItem.of(oldFile.fileId(), commitItems)));
        return commitChanges(project.projectId(), author, commitData);
    }

    private void updateFileContent(@Nonnull Project project,
                                   @Nonnull FileId fileId,
                                   @Nonnull File newFile) {
        project.updateFile(fileId, newFile.content());
        this.projectRepository.save(project);
    }

    private boolean commitChanges(@Nonnull ProjectId projectId,
                                  @Nonnull String author,
                                  @Nonnull CommitDescription commitData) {
        Long parentId = this.commitRepository.findByProjectIdOrderById(projectId)
            .stream()
            .map(Commit::id)
            .max(Long::compareTo)
            .orElse(null);
        Commit newCommit = Commit.builder()
            .parentId(parentId)
            .projectId(projectId)
            .author(author)
            .data(commitData)
            .build();
        this.commitRepository.save(newCommit);
        return true;
    }

}

