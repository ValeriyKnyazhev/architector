package valeriy.knyazhev.architector.application.project;

import lombok.RequiredArgsConstructor;
import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valeriy.knyazhev.architector.application.project.command.CreateProjectCommand;
import valeriy.knyazhev.architector.application.project.command.UpdateProjectDescriptionCommand;
import valeriy.knyazhev.architector.application.project.command.UpdateProjectNameCommand;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RequiredArgsConstructor
@Service
@Transactional
public class ProjectManagementService {

    private final ProjectRepository projectRepository;

    @Nonnull
    public ProjectId createProject(@Nonnull CreateProjectCommand command) {
        Args.notNull(command, "Create project command is required.");
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName(command.name())
            .withAuthor(command.author())
            .withDescription(command.description())
            .construct();
        return this.projectRepository.save(project).projectId();
    }

    public void updateProjectName(@Nonnull UpdateProjectNameCommand command)
    {
        Args.notNull(command, "Update project name command is required.");
        ProjectId projectId = command.projectId();
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        project.updateName(command.name());
        this.projectRepository.save(project);
    }

    public void updateProjectDescription(@Nonnull UpdateProjectDescriptionCommand command)
    {
        Args.notNull(command, "Update project description command is required.");
        ProjectId projectId = command.projectId();
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        project.updateDescription(command.description());
        this.projectRepository.save(project);
    }

}

