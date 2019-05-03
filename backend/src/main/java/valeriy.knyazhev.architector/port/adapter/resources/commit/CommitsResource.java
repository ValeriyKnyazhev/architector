package valeriy.knyazhev.architector.port.adapter.resources.commit;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import valeriy.knyazhev.architector.application.commit.CommitApplicationService;
import valeriy.knyazhev.architector.application.commit.command.FindCommitsCommand;
import valeriy.knyazhev.architector.application.commit.data.FileHistoryData;
import valeriy.knyazhev.architector.application.commit.data.ProjectHistoryData;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.Commit;
import valeriy.knyazhev.architector.domain.model.commit.CommitCombinator;
import valeriy.knyazhev.architector.domain.model.commit.projection.ProjectDataProjection;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.port.adapter.resources.commit.model.ProjectContentModel;
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
public class CommitsResource
{

    private final CommitApplicationService applicationService;

    public CommitsResource(@Nonnull CommitApplicationService applicationService)
    {
        this.applicationService = Args.notNull(applicationService, "Commit application service is required.");
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

    @GetMapping(value = "api/projects/{qProjectId}/commits",
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchProjectChanges(@PathVariable String qProjectId)
    {
        ProjectHistoryData projectHistory = (ProjectHistoryData) this.applicationService.fetchProjectHistory(
            FindCommitsCommand.builder()
                .projectId(qProjectId)
                .build()
        );
        return ResponseEntity.ok(projectHistory);
    }

    @GetMapping(value = "api/projects/{qProjectId}/files/{qFileId}/commits",
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchFileChanges(@PathVariable String qProjectId,
                                                   @PathVariable String qFileId)
    {
        FileHistoryData fileHistory = (FileHistoryData) this.applicationService.fetchProjectHistory(
            FindCommitsCommand.builder()
                .projectId(qProjectId)
                .fileId(qFileId)
                .build()
        );
        return ResponseEntity.ok(fileHistory);
    }

    @GetMapping(value = "api/projects/{qProjectId}/commits/{commitId}/content",
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchProjectChanges(@PathVariable String qProjectId,
                                                      @PathVariable long commitId)
    {
        ProjectId projectId = ProjectId.of(qProjectId);
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
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
        List<Commit> projectHistory = commits.stream()
            .filter(commit -> identifiers.contains(commit.id()))
            .sorted(Comparator.comparingLong(Commit::id))
            .collect(toList());
        ProjectDataProjection projection = CommitCombinator.combineCommits(projectHistory);
        return ResponseEntity.ok(
            new ProjectContentModel(
                projectId.id(),
                project.name(),
                projection.files().stream()
                    .filter(file -> !file.items().isEmpty())
                    .map(file -> new FileContentModel(
                            file.fileId().id(),
                            file.metadata().name(),
                            file.items()
                        )
                    )
                    .collect(toList())
            )
        );
    }

}
