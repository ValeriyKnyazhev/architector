package valeriy.knyazhev.architector.port.adapter.project;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.application.project.IFCProjectReader;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static valeriy.knyazhev.architector.port.adapter.project.model.ProjectMapper.mapToModel;

/**
 * @author Valeriy Knyazhev
 */
@RestController
public class ProjectResource {

    private final ProjectRepository projectRepository;

    private final IFCProjectReader projectReader;

    public ProjectResource(@Nonnull ProjectRepository projectRepository,
                           @Nonnull IFCProjectReader projectReader) {
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
        this.projectReader = Args.notNull(projectReader, "Project reader is required.");
    }

    @PostMapping(value = "/projects/source",
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> createFromUrl(@RequestBody AddFileFromUrlCommand command) {
        Args.notNull(command, "Create project from url command is required.");
        try {
            URL projectUrl = new URL(command.fileUrl());
            Project project = this.projectReader.readFromUrl(projectUrl);
            projectRepository.save(project);
            return ResponseEntity.ok().body(new ResponseMessage()
                    .info("Project " + project.projectId().id() + " was created from source URL."));
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage().error(e.getMessage()));
        }
    }

    @PostMapping(value = "/projects/import",
            consumes = MULTIPART_FORM_DATA_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseMessage> createFromFile(@RequestParam("file") MultipartFile multipartFile) {
        try {
            Project project = this.projectReader.readFromFile(multipartFile.getInputStream());
            projectRepository.save(project);
            return ResponseEntity.ok().body(new ResponseMessage()
                    .info("Project " + project.projectId().id() + " was created from received file."));
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage().error(e.getMessage()));
        } catch (IOException ex) {
            return ResponseEntity.badRequest().body(new ResponseMessage()
                    .error("Unexpected IO error: try to import project " + multipartFile.getOriginalFilename() + " one more time."));
        }
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
