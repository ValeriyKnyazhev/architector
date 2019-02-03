package valeriy.knyazhev.architector.port.adapter.project;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.application.project.CreateProjectFromFileCommand;
import valeriy.knyazhev.architector.application.project.CreateProjectFromUrlCommand;
import valeriy.knyazhev.architector.application.project.ProjectManagementService;
import valeriy.knyazhev.architector.application.project.ProjectQueryService;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;

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

    @GetMapping(value = "/projects/{qProjectId}",
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> findProject(@PathVariable String qProjectId) {
        Project project = this.queryService.findById(qProjectId);
        if (project == null) {
            return ResponseEntity.status(NOT_FOUND).body(new ResponseMessage()
                    .error("Project with identifier " + qProjectId + " not found."));
        }
        return ResponseEntity.ok(mapToModel(project));
    }

}
