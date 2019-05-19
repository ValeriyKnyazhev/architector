package valeriy.knyazhev.architector.port.adapter.resources.project;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import valeriy.knyazhev.architector.application.project.ProjectData;
import valeriy.knyazhev.architector.application.project.ProjectManagementService;
import valeriy.knyazhev.architector.application.project.ProjectQueryService;
import valeriy.knyazhev.architector.application.project.command.CreateProjectCommand;
import valeriy.knyazhev.architector.application.project.command.UpdateProjectDataCommand;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.port.adapter.resources.project.model.ProjectDescriptorModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.model.ProjectMapper;
import valeriy.knyazhev.architector.port.adapter.resources.project.request.CreateProjectRequest;
import valeriy.knyazhev.architector.port.adapter.resources.project.request.UpdateProjectDataRequest;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static valeriy.knyazhev.architector.port.adapter.resources.project.model.ProjectMapper.buildProject;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RestController
public class ProjectResource
{

    private ProjectManagementService managementService;

    private ProjectQueryService queryService;

    public ProjectResource(@Nonnull ProjectManagementService managementService,
                           @Nonnull ProjectQueryService queryService)
    {
        this.managementService = Args.notNull(managementService, "Management service is required.");
        this.queryService = Args.notNull(queryService, "Query service is required.");
    }

    @PostMapping(value = "/api/projects/",
                 consumes = APPLICATION_JSON_UTF8_VALUE,
                 produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> createProject(@RequestBody @Valid CreateProjectRequest request,
                                                @Nonnull Architector architector)
    {
        ProjectId projectId = this.managementService.createProject(
            new CreateProjectCommand(request.name(), architector.email(), request.description()));
        return ResponseEntity.ok()
            .body(
                new ResponseMessage().info("Project " + projectId.id() + " was created.")
            );
    }

    @GetMapping(value = "/api/projects", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> findProjects(@Nonnull Architector architector)
    {
        List<ProjectDescriptorModel> projects = this.queryService.findProjects(architector).stream()
            .map(ProjectMapper::buildProject)
            .collect(toList());
        return ResponseEntity.ok(Collections.singletonMap("projects", projects));
    }

    @GetMapping(value = "/api/projects/{qProjectId}", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> findProject(@PathVariable String qProjectId,
                                              @Nonnull Architector architector)
    {
        ProjectData project = this.queryService.findById(qProjectId, architector);
        if (project == null)
        {
            return ResponseEntity.status(NOT_FOUND).body(new ResponseMessage()
                .error("Project with identifier " + qProjectId + " not found."));
        }
        return ResponseEntity.ok(buildProject(project));
    }

    @PutMapping(value = "/api/projects/{qProjectId}",
                consumes = APPLICATION_JSON_UTF8_VALUE,
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> updateProjectData(@PathVariable String qProjectId,
                                                    @RequestBody @Valid UpdateProjectDataRequest request,
                                                    @Nonnull Architector architector)
    {
        this.managementService.updateProjectData(
            new UpdateProjectDataCommand(
                qProjectId, request.name(), request.description(), architector.email()
            )
        );
        return ResponseEntity.ok()
            .body(
                new ResponseMessage().info("Project " + qProjectId + " data was updated.")
            );
    }

}
