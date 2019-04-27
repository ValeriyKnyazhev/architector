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
import valeriy.knyazhev.architector.domain.model.project.commit.CommitRepository;
import valeriy.knyazhev.architector.port.adapter.resources.project.commit.model.CommitBriefModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.commit.model.ProjectChangesModel;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RestController
public class ProjectChangesResource {

    private final CommitRepository commitRepository;

    private final ProjectRepository projectRepository;

    public ProjectChangesResource(@Nonnull CommitRepository commitRepository,
                                  @Nonnull ProjectRepository projectRepository) {
        this.commitRepository = Args.notNull(commitRepository, "Commit repository is required.");
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
    }

    @Nonnull
    private static CommitBriefModel constructBriefDescription(@Nonnull Commit commit) {
        return new CommitBriefModel(
            commit.id(),
            commit.parentId(),
            commit.author(),
            commit.message(),
            commit.timestamp()
        );
    }

    @GetMapping(value = "api/projects/{qProjectId}/changes", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchProjectChanges(@PathVariable String qProjectId) {
        ProjectId projectId = ProjectId.of(qProjectId);
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        List<CommitBriefModel> changes = this.commitRepository.findByProjectIdOrderById(projectId).stream()
            .map(ProjectChangesResource::constructBriefDescription)
            .collect(toList());
        return ResponseEntity.ok(
            new ProjectChangesModel(
                projectId.id(),
                project.name(),
                changes
            )
        );
    }

}
