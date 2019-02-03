package valeriy.knyazhev.architector.application.project;

import lombok.RequiredArgsConstructor;
import org.apache.http.util.Args;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.application.project.command.CreateProjectFromFileCommand;
import valeriy.knyazhev.architector.application.project.command.CreateProjectFromUrlCommand;
import valeriy.knyazhev.architector.application.project.command.UpdateProjectFromFileCommand;
import valeriy.knyazhev.architector.application.project.command.UpdateProjectFromUrlCommand;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Component
@RequiredArgsConstructor
public class ProjectManagementService {


    private final ProjectRepository projectRepository;

    private final IFCProjectReader projectReader;

    @Nonnull
    public ProjectId createProject(@Nonnull CreateProjectFromUrlCommand command) {
        Args.notNull(command, "Create project from url command is required.");
        try {
            URL projectUrl = new URL(command.sourceUrl());
            Project project = this.projectReader.readFromUrl(projectUrl);
            return this.projectRepository.save(project).projectId();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public ProjectId createProject(@Nonnull CreateProjectFromFileCommand command) {
        try {
            MultipartFile file = command.file();
            Project project = this.projectReader.readFromFile(file.getInputStream());
            return projectRepository.save(project).projectId();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (IOException ex) {
            throw new IllegalStateException("Unexpected IO error: try to import project one more time.");
        }
    }

    public boolean updateProject(UpdateProjectFromUrlCommand command) {
        return false;
    }

    public boolean updateProject(UpdateProjectFromFileCommand command) {
        return false;
    }
}
