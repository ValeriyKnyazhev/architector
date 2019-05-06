package valeriy.knyazhev.architector.application.project;

import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valeriy.knyazhev.architector.application.project.command.CreateProjectCommand;
import valeriy.knyazhev.architector.application.project.command.UpdateProjectDataCommand;
import valeriy.knyazhev.architector.domain.model.commit.Commit;
import valeriy.knyazhev.architector.domain.model.commit.CommitDescription;
import valeriy.knyazhev.architector.domain.model.commit.CommitRepository;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;

import javax.annotation.Nonnull;

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

    public ProjectManagementService(@Nonnull ProjectRepository projectRepository,
                                    @Nonnull CommitRepository commitRepository)
    {
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
        this.commitRepository = Args.notNull(commitRepository, "Commit repository is required.");
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
        return this.projectRepository.save(project).projectId();
    }

    public boolean updateProjectData(@Nonnull UpdateProjectDataCommand command)
    {
        Args.notNull(command, "Update project data command is required.");
        ProjectId projectId = command.projectId();
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        boolean updated = project.updateName(command.name()) || project.updateDescription(command.description());
        CommitDescription commitData = CommitDescription.builder()
            .name(updated ? command.name() : null)
            .description(updated ? command.description() : null)
            .files(emptyList())
            .build();
        this.projectRepository.save(project);
        return commitChanges(
            project.projectId(),
            command.author(),
            "Project " + projectId.id() + " data was updated.",
            commitData);
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

}

