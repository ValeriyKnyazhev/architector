package valeriy.knyazhev.architector.application.commit;

import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.Commit;
import valeriy.knyazhev.architector.domain.model.commit.CommitCombinator;
import valeriy.knyazhev.architector.domain.model.commit.CommitRepository;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Service
@Transactional
public class ProjectionConstructService
{

    private final CommitRepository commitRepository;

    private final ProjectRepository projectRepository;

    public ProjectionConstructService(@Nonnull CommitRepository commitRepository,
                                      @Nonnull ProjectRepository projectRepository)
    {
        this.commitRepository = Args.notNull(commitRepository, "Commit repository is required.");
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
    }


    @Nonnull
    public Projection makeProjection(@Nonnull ProjectId projectId,
                                     @Nullable Long commitId)
    {
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        List<Commit> history = commitId != null ? extractHistoryForId(projectId, commitId) : List.of();
        return CommitCombinator.combineCommits(project.name(), project.description(), history);
    }

    @Nonnull
    private List<Commit> extractHistoryForId(@Nonnull ProjectId projectId,
                                             long commitId)
    {
        List<Commit> commits = this.commitRepository.findByProjectIdOrderById(projectId);
        //TODO optimize
        List<Long> identifiers = new LinkedList<>();
        Long lastParentCommitId = findCommitById(commits, commitId).parentId();
        identifiers.add(commitId);
        while (lastParentCommitId != null)
        {
            identifiers.add(lastParentCommitId);
            lastParentCommitId = findCommitById(commits, lastParentCommitId).parentId();
        }
        return commits.stream()
            .filter(commit -> identifiers.contains(commit.id()))
            .sorted(Comparator.comparingLong(Commit::id))
            .collect(toList());
    }

    @Nonnull
    private static Commit findCommitById(@Nonnull List<Commit> commits,
                                         long commitId)
    {
        return commits.stream()
            .filter(commit -> commitId == commit.id())
            .findFirst()
            .orElseThrow(() -> new CommitNotFoundException(commitId));
    }

}

