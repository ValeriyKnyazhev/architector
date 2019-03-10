package valeriy.knyazhev.architector.application.project;

import lombok.RequiredArgsConstructor;
import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.application.project.command.CreateProjectFromFileCommand;
import valeriy.knyazhev.architector.application.project.command.CreateProjectFromUrlCommand;
import valeriy.knyazhev.architector.application.project.command.UpdateProjectFromFileCommand;
import valeriy.knyazhev.architector.application.project.command.UpdateProjectFromUrlCommand;
import valeriy.knyazhev.architector.application.project.file.IFCFileReader;
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
public class ProjectManagementService {


    private final ProjectRepository projectRepository;

    private final IFCProjectReader projectReader;

    private final IFCFileReader fileReader;

    private final FileDiffCalculator diffCalculator;

    private final CommitRepository commitRepository;

    @Nonnull
    public ProjectId createProject(@Nonnull CreateProjectFromUrlCommand command) {
        Args.notNull(command, "Create project from url command is required.");
        try {
            URL projectUrl = new URL(command.sourceUrl());
            Project project = this.projectReader.readFromUrl(projectUrl);
            // FIXME
            File file = project.files().stream().findFirst().orElse(null);
            CommitDescription commitData = CommitDescription.of(singletonList(
                    CommitFileItem.of(file.fileId(), this.diffCalculator.calculateDiff(null, file))));
            commitChanges(project.projectId(), command.author(), commitData);
            return this.projectRepository.save(project).projectId();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Nonnull
    public ProjectId createProject(@Nonnull CreateProjectFromFileCommand command) {
        Args.notNull(command, "Create project from file command is required.");
        try {
            MultipartFile multipartFile = command.file();
            Project project = this.projectReader.readFromFile(multipartFile.getInputStream());
            // FIXME if project has more than 1 file
            File file = project.files().stream().findFirst().orElse(null);
            CommitDescription commitData = CommitDescription.of(singletonList(
                    CommitFileItem.of(file.fileId(), this.diffCalculator.calculateDiff(null, file))));
            commitChanges(project.projectId(), command.author(), commitData);
            return projectRepository.save(project).projectId();
        } catch (IOException ex) {
            throw new IllegalStateException("Unexpected IO error: try to import project one more time.");
        }
    }

    public boolean updateProject(UpdateProjectFromUrlCommand command) {
        Args.notNull(command, "Update project from url command is required.");
        File newFile = null;
        try {
            newFile = this.fileReader.readFromUrl(new URL(command.sourceUrl()));
        } catch (MalformedURLException e) {
            return false;
        }
        return updateProject(command.projectId(), command.author(), newFile);
    }

    public boolean updateProject(UpdateProjectFromFileCommand command) {
        Args.notNull(command, "Update project from file command is required.");
        File newFile = null;
        try {
            MultipartFile multipartFile = command.file();
            newFile = this.fileReader.readFromFile(multipartFile.getInputStream());
        } catch (IOException ex) {
            return false;
        }
        return updateProject(command.projectId(), command.author(), newFile);
    }

    private boolean updateProject(@Nonnull ProjectId projectId, @Nonnull String author, @Nonnull File newFile) {
        Project project = this.projectRepository.findByProjectId(projectId).orElse(null);
        if (project == null) {
            throw new IllegalStateException("Project not found.");
        }

        File oldFile = project.files().isEmpty() ? null : project.files().get(0);
        List<CommitItem> commitItems = this.diffCalculator.calculateDiff(oldFile, newFile);
        if (commitItems.isEmpty()) {
            // TODO return answer that nothing to commit
            return false;
        }
        FileId fileId = updateProjectContent(project, oldFile, newFile);
        CommitDescription commitData = CommitDescription.of(
                singletonList(CommitFileItem.of(fileId, commitItems)));
        return commitChanges(project.projectId(), author, commitData);
    }

    private FileId updateProjectContent(@Nonnull Project project,
                                        @Nullable File oldFile,
                                        @Nonnull File newFile) {
        FileId fileId = null;
        if (oldFile == null) {
            fileId = newFile.fileId();
            project.addFile(newFile);
        } else {
            fileId = oldFile.fileId();
            project.updateFile(fileId, newFile.content());
        }
        this.projectRepository.save(project);
        return fileId;
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

