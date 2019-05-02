package valeriy.knyazhev.architector.port.adapter.resources.project.commit;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.commit.Commit;
import valeriy.knyazhev.architector.domain.model.project.commit.CommitCombinator;
import valeriy.knyazhev.architector.domain.model.project.commit.CommitRepository;
import valeriy.knyazhev.architector.domain.model.project.commit.projection.ProjectDataProjection;
import valeriy.knyazhev.architector.port.adapter.resources.project.commit.model.ProjectCommitBriefModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.commit.model.ProjectCommitsModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.commit.model.ProjectContentModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.FileContentModel;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RestController
public class ProjectCommitsResource {

    private final CommitRepository commitRepository;

    private final ProjectRepository projectRepository;

    private final CommitCombinator commitCombinator;

    public ProjectCommitsResource(@Nonnull CommitRepository commitRepository,
                                  @Nonnull ProjectRepository projectRepository,
                                  @Nonnull CommitCombinator commitCombinator) {
        this.commitRepository = Args.notNull(commitRepository, "Commit repository is required.");
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
        this.commitCombinator = Args.notNull(commitCombinator, "Commit combinator is required.");
    }

    @Nonnull
    private static ProjectCommitBriefModel constructBriefDescription(@Nonnull Commit commit) {
        return new ProjectCommitBriefModel(
            commit.id(),
            commit.parentId(),
            commit.author(),
            commit.message(),
            commit.timestamp()
        );
    }

    @Nonnull
    private static Commit findCommitById(@Nonnull List<Commit> commits,
                                         long commitId) {
        return commits.stream()
            .filter(commit -> commitId == commit.id())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                    "Commit with id " + commitId + " not found."
                )
            );
    }

    @GetMapping(value = "api/projects/{qProjectId}/commits",
        produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchProjectChanges(@PathVariable String qProjectId) {
        ProjectId projectId = ProjectId.of(qProjectId);
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        List<ProjectCommitBriefModel> commits = this.commitRepository
            .findByProjectIdOrderByIdDesc(projectId)
            .stream()
            .map(ProjectCommitsResource::constructBriefDescription)
            .collect(toList());
        return ResponseEntity.ok(
            new ProjectCommitsModel(
                projectId.id(),
                project.name(),
                commits
            )
        );
    }

    @GetMapping(value = "api/projects/{qProjectId}/commits/{commitId}/content",
        produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchProjectChanges(@PathVariable String qProjectId,
                                                      @PathVariable long commitId) {
        ProjectId projectId = ProjectId.of(qProjectId);
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        List<Commit> commits = this.commitRepository.findByProjectIdOrderById(projectId);
        //TODO optimize
        List<Long> identifiers = new LinkedList<>();
        Long lastParentCommitId = findCommitById(commits, commitId).parentId();
        identifiers.add(commitId);
        while (lastParentCommitId != null) {
            identifiers.add(lastParentCommitId);
            lastParentCommitId = findCommitById(commits, lastParentCommitId).parentId();
        }
        List<Commit> projectHistory = commits.stream()
            .filter(commit -> identifiers.contains(commit.id()))
            .sorted(Comparator.comparingLong(Commit::id))
            .collect(toList());
        ProjectDataProjection projection = this.commitCombinator.combineCommits(projectHistory);
        return ResponseEntity.ok(
            new ProjectContentModel(
                projectId.id(),
                project.name(),
                projection.files().stream()
                    .filter(file -> !file.items().isEmpty())
                    .map(file -> new FileContentModel(
                            file.fileId().id(),
                        // FIXME add name from commit
                        "FIXME",
                            file.items()
                        )
                    )
                    .collect(toList())
            )
        );
    }
}
