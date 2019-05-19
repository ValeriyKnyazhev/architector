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

    private final CommitRepository commitRepository;

    private final ArchitectorRepository architectorRepository;

    public ProjectManagementService(@Nonnull ProjectRepository projectRepository,
                                    @Nonnull CommitRepository commitRepository,
                                    @Nonnull ArchitectorRepository architectorRepository)
    {
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
        this.commitRepository = Args.notNull(commitRepository, "Commit repository is required.");
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
        CommitDescription commitData = CommitDescription.builder()
            .name(project.name())
            .description(project.description())
            .files(emptyList())
            .build();
        boolean added = commitChanges(
            project.projectId(),
            command.author(),
            "Project was created.",
            commitData);
        if (!added)
        {
            throw new IllegalStateException("Something went wrong during creating project.");
        }
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
        if (nameUpdated || descriptionUpdated)
        {
            CommitDescription commitData = CommitDescription.builder()
                .name(nameUpdated ? command.name() : null)
                .description(descriptionUpdated ? command.description() : null)
                .files(emptyList())
                .build();
            return commitChanges(
                project.projectId(),
                command.author(),
                "Project " + projectId.id() + " data was updated.",
                commitData);
        } else
        {
            return false;
        }
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
        } else {
            throw new AccessRightsNotFoundException();
        }
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
        this.commitRepository.saveAndFlush(newCommit);
        return true;
    }

}

