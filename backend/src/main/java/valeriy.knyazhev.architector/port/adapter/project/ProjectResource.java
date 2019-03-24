package valeriy.knyazhev.architector.port.adapter.project;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.application.project.ProjectManagementService;
import valeriy.knyazhev.architector.application.project.ProjectQueryService;
import valeriy.knyazhev.architector.application.project.command.CreateProjectFromFileCommand;
import valeriy.knyazhev.architector.application.project.command.CreateProjectFromUrlCommand;
import valeriy.knyazhev.architector.application.project.command.UpdateProjectFromFileCommand;
import valeriy.knyazhev.architector.application.project.command.UpdateProjectFromUrlCommand;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.port.adapter.project.model.ProjectMapper;
import valeriy.knyazhev.architector.port.adapter.project.model.ProjectModel;
import valeriy.knyazhev.architector.port.adapter.project.request.CreateProjectFromUrlRequest;
import valeriy.knyazhev.architector.port.adapter.project.request.UpdateProjectFromUrlRequest;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static valeriy.knyazhev.architector.port.adapter.project.model.ProjectMapper.mapToModel;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RestController
public class ProjectResource {

    private ProjectManagementService managementService;

    private ProjectQueryService queryService;

    public ProjectResource(@Nonnull ProjectManagementService managementService,
                           @Nonnull ProjectQueryService queryService) {
        this.managementService = Args.notNull(managementService, "Management service is required.");
        this.queryService = Args.notNull(queryService, "Query service is required.");
    }

    // TODO add resources for creating and updating projects from json content (for webclient)

    @PostMapping(value = "/projects/source",
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> createFromUrl(@RequestBody CreateProjectFromUrlRequest request) {
        ProjectId projectId = this.managementService.createProject(
                // TODO author constant should be replaced by user email
                new CreateProjectFromUrlCommand("author", request.sourceUrl()));
        return ResponseEntity.ok().body(new ResponseMessage()
                .info("Project " + projectId.id() + " was created from source URL."));
    }

    @PostMapping(value = "/projects/import",
            consumes = MULTIPART_FORM_DATA_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseMessage> createFromFile(@RequestParam("file") MultipartFile multipartFile) {
        ProjectId projectId = this.managementService.createProject(
                // TODO author constant should be replaced by user email
                new CreateProjectFromFileCommand("author", multipartFile));
        return ResponseEntity.ok().body(new ResponseMessage()
                .info("Project " + projectId.id() + " was created from received file."));
    }

    @PutMapping(value = "/projects/{qProjectId}/source",
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> updateFromUrl(@PathVariable String qProjectId,
                                                @RequestBody UpdateProjectFromUrlRequest request) {
        boolean updated = this.managementService.updateProject(
                // TODO author constant should be replaced by user email
                new UpdateProjectFromUrlCommand(qProjectId, "author", request.sourceUrl()));
        return updated
                ? ResponseEntity.ok().body(
                new ResponseMessage().info("Project " + qProjectId + " was updated from source URL."))
                : ResponseEntity.badRequest().body(
                new ResponseMessage().error("Unable to update project " + qProjectId + " from source URL."));
    }

    @PutMapping(value = "/projects/{qProjectId}/import",
            consumes = MULTIPART_FORM_DATA_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseMessage> updateFromFile(@PathVariable String qProjectId,
                                                          @RequestParam("file") MultipartFile multipartFile) {
        boolean updated = this.managementService.updateProject(
                // TODO author constant should be replaced by user email
                new UpdateProjectFromFileCommand(qProjectId, "author", multipartFile));
        return updated
                ? ResponseEntity.ok().body(
                new ResponseMessage().info("Project " + qProjectId + " was updated from received file."))
                : ResponseEntity.badRequest().body(
                new ResponseMessage().error("Unable to update project " + qProjectId + " from received file."));
    }

    @GetMapping(value = "/projects", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> findAllProjects() {
        List<ProjectModel> projects = this.queryService.findAllProjects().stream()
                .map(ProjectMapper::mapToModel)
                .collect(toList());
        return ResponseEntity.ok(Collections.singletonMap("projects", projects));
    }

    @GetMapping(value = "/projects/{qProjectId}", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> findProject(@PathVariable String qProjectId) {
        Project project = this.queryService.findById(qProjectId);
        if (project == null) {
            return ResponseEntity.status(NOT_FOUND).body(new ResponseMessage()
                    .error("Project with identifier " + qProjectId + " not found."));
        }
        return ResponseEntity.ok(mapToModel(project));
    }

}
