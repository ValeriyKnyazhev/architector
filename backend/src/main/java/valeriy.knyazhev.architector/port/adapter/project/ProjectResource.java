package valeriy.knyazhev.architector.port.adapter.project;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static valeriy.knyazhev.architector.port.adapter.project.model.ProjectMapper.mapToModel;

/**
 * @author Valeriy Knyazhev
 */
@RestController
public class ProjectResource {

    private final ProjectRepository projectRepository;

    public ProjectResource(@Nonnull ProjectRepository projectRepository) {
        this.projectRepository = Args.notNull(projectRepository, "Project repository");
    }

    @PostMapping(value = "/projects",
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> importDataFromUrl() {
        Project project = Project.constructor().projectId(ProjectId.nextId()).construct();
        projectRepository.save(project);
        return ResponseEntity.ok().body(new ResponseMessage()
                .info("Project " + project.projectId().id() + " was created."));
    }

    @GetMapping(value = "/projects/{qProjectId}",
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> findProject(@PathVariable String qProjectId) {
        Args.notNull(qProjectId, "Project identifier is required.");
        ProjectId projectId = ProjectId.of(qProjectId);
        Project project = this.projectRepository.findByProjectId(projectId).orElse(null);
        if (project == null) {
            return ResponseEntity.status(NOT_FOUND).body(new ResponseMessage()
                    .error("Project with identifier " + qProjectId + " not found."));
        }
        return ResponseEntity.ok(mapToModel(project));
    }

}
