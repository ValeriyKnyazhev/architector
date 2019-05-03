package valeriy.knyazhev.architector.application.commit;

import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valeriy.knyazhev.architector.application.commit.command.FindCommitsCommand;
import valeriy.knyazhev.architector.application.commit.data.AbstractHistoryData;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.Commit;
import valeriy.knyazhev.architector.domain.model.commit.CommitRepository;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Service
@Transactional
public class CommitApplicationService
{

    private final CommitRepository commitRepository;

    private final ProjectRepository projectRepository;

    public CommitApplicationService(@Nonnull CommitRepository commitRepository,
                                    @Nonnull ProjectRepository projectRepository)
    {
        this.commitRepository = Args.notNull(commitRepository, "Commit repository is required.");
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
    }

    @Nonnull
    public AbstractHistoryData fetchProjectHistory(@Nonnull FindCommitsCommand command)
    {
        Args.notNull(command, "Find commit command is required.");
        ProjectId projectId = command.projectId();
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        List<Commit> commits = this.commitRepository.findByProjectIdOrderByIdDesc(projectId);
        return command.constructHistory(project, commits);
    }

}

