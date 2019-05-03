package valeriy.knyazhev.architector.application.commit;

import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valeriy.knyazhev.architector.application.commit.command.FindCommitsCommand;
import valeriy.knyazhev.architector.application.commit.command.MakeFileProjectionCommand;
import valeriy.knyazhev.architector.application.commit.command.MakeProjectProjectionCommand;
import valeriy.knyazhev.architector.application.commit.data.AbstractHistoryData;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.Commit;
import valeriy.knyazhev.architector.domain.model.commit.CommitCombinator;
import valeriy.knyazhev.architector.domain.model.commit.CommitRepository;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection.FileProjection;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

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
    private static Commit findCommitById(@Nonnull List<Commit> commits,
                                         long commitId)
    {
        return commits.stream()
            .filter(commit -> commitId == commit.id())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                    "Commit with id " + commitId + " not found."
                )
            );
    }

    @Nonnull
    public AbstractHistoryData fetchProjectHistory(@Nonnull FindCommitsCommand command)
    {
        Args.notNull(command, "Find commit command is required.");
        ProjectId projectId = command.projectId();
        Project project = findProject(projectId);
        List<Commit> commits = this.commitRepository.findByProjectIdOrderByIdDesc(projectId);
        return command.constructHistory(project, commits);
    }

    @Nonnull
    public Projection makeProjection(@Nonnull MakeProjectProjectionCommand command)
    {
        ProjectId projectId = command.projectId();
        Project project = findProject(projectId);
        List<Commit> history = extractHistoryForId(projectId, command.commitId());
        return CommitCombinator.combineCommits(history);
    }

    @Nonnull
    public FileProjection makeProjection(@Nonnull MakeFileProjectionCommand command)
    {
        ProjectId projectId = command.projectId();
        Project project = findProject(projectId);
        List<Commit> history = extractHistoryForId(projectId, command.commitId());
        Projection projection = CommitCombinator.combineCommits(history);
        FileId fileId = command.fileId();
        return projection.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId));
    }

    @Nonnull
    private Project findProject(@Nonnull ProjectId projectId)
    {
        return this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
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

}

