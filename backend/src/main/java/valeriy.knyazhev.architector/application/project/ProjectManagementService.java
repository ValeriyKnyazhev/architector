package valeriy.knyazhev.architector.application.project;

import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import valeriy.knyazhev.architector.application.project.command.*;
import valeriy.knyazhev.architector.application.user.ArchitectorNotFoundException;
import valeriy.knyazhev.architector.domain.model.AccessRightsNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.Commit;
import valeriy.knyazhev.architector.domain.model.commit.CommitDescription;
import valeriy.knyazhev.architector.domain.model.commit.CommitRepository;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.domain.model.user.ArchitectorRepository;

import javax.annotation.Nonnull;
import javax.security.auth.login.AccountNotFoundException;

import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Service
@Transactional
public class ProjectManagementService
{

    private final ProjectRepository projectRepository;

    private final ArchitectorRepository architectorRepository;

    public ProjectManagementService(@Nonnull ProjectRepository projectRepository,
                                    @Nonnull ArchitectorRepository architectorRepository)
    {
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
        this.architectorRepository = Args.notNull(architectorRepository, "Architector repository is required.");
    }

    @Nonnull
    public ProjectId createProject(@Nonnull CreateProjectCommand command)
    {
        Args.notNull(command, "Create project command is required.");
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName(command.name())
            .withAuthor(command.author())
            .withDescription(command.description())
            .construct();
        return this.projectRepository.saveAndFlush(project).projectId();
    }

    public boolean updateProjectData(@Nonnull Architector architector,
                                     @Nonnull UpdateProjectDataCommand command)
    {
        Args.notNull(architector, "Architector is required.");
        Args.notNull(command, "Update project data command is required.");
        ProjectId projectId = command.projectId();
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        if (!project.canBeUpdated(architector))
        {
            throw new AccessRightsNotFoundException();
        }
        boolean nameUpdated = project.updateName(command.name());
        boolean descriptionUpdated = project.updateDescription(command.description());
        this.projectRepository.saveAndFlush(project);
        return nameUpdated || descriptionUpdated;
    }

    public void addReadAccessRights(@Nonnull Architector architector,
                                    @Nonnull AddReadAccessRightsCommand command)
    {
        ProjectId projectId = command.projectId();
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        if (architector.isAdmin() || project.author().equals(architector.email())){
            Architector requestedArchitector = this.architectorRepository.findByEmail(command.architector())
                .orElseThrow(()-> new ArchitectorNotFoundException(command.architector()));
            project.addReadAccessRights(requestedArchitector);
            this.projectRepository.saveAndFlush(project);
        } else {
            throw new AccessRightsNotFoundException();
        }
    }

    public void addWriteAccessRights(@Nonnull Architector architector,
                                     @Nonnull AddWriteAccessRightsCommand command)
    {
        ProjectId projectId = command.projectId();
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        if (architector.isAdmin() || project.author().equals(architector.email())){
            Architector requestedArchitector = this.architectorRepository.findByEmail(command.architector())
                .orElseThrow(()-> new ArchitectorNotFoundException(command.architector()));
            project.addWriteAccessRights(requestedArchitector);
            this.projectRepository.saveAndFlush(project);
        } else {
            throw new AccessRightsNotFoundException();
        }
    }

    public void takeAwayAccessRights(@Nonnull Architector architector,
                                     @Nonnull TakeAwayAccessRightsCommand command)
    {
        ProjectId projectId = command.projectId();
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        if (architector.isAdmin() || project.author().equals(architector.email())){
            Architector requestedArchitector = this.architectorRepository.findByEmail(command.architector())
                .orElseThrow(()-> new ArchitectorNotFoundException(command.architector()));
            project.takeAwayAccessRights(requestedArchitector);
            this.projectRepository.saveAndFlush(project);
        } else {
            throw new AccessRightsNotFoundException();
        }
    }

}

