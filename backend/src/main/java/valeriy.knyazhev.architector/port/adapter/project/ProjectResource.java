package valeriy.knyazhev.architector.port.adapter.project;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import valeriy.knyazhev.architector.application.IFCProjectReader;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static valeriy.knyazhev.architector.port.adapter.project.model.ProjectMapper.mapToModel;

/**
 * @author Valeriy Knyazhev
 */
@RestController
public class ProjectResource {

    private final IFCProjectReader projectReader;

    private final ProjectRepository projectRepository;

    public ProjectResource(@Nonnull IFCProjectReader projectReader,
                           @Nonnull ProjectRepository projectRepository) {
        this.projectReader = Args.notNull(projectReader, "Project reader");
        this.projectRepository = Args.notNull(projectRepository, "Project repository");
    }

    @PostMapping(value = "/projects",
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> importDataFromUrl(@RequestBody CreateProjectCommand command) {
        Args.notNull(command, "Read project command is required.");
        try {
            URL projectUrl = new URL(command.projectUrl());
            Project project = this.projectReader.readProjectFromUrl(projectUrl);
            projectRepository.save(project);
            return ResponseEntity.ok()
                    .body(new ResponseMessage().info("Project " + project.projectId().id() + " was created."));
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage().error(e.getMessage()));
        }
    }

    @GetMapping(value = "/projects/{qProjectId}",
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> findProject(@PathVariable String qProjectId) {
        Args.notNull(qProjectId, "Project identifier is required.");
        ProjectId projectId = ProjectId.of(qProjectId);
        Project project = this.projectRepository.findByProjectId(projectId).orElse(null);
        if (project == null) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ResponseMessage().error("Project with identifier " + qProjectId + " not found."));
        }
        return ResponseEntity.ok(mapToModel(project));
    }

}
