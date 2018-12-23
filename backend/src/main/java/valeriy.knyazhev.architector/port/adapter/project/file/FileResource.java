package valeriy.knyazhev.architector.port.adapter.project.file;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import valeriy.knyazhev.architector.application.IFCFileReader;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.port.adapter.project.AddFileCommand;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static valeriy.knyazhev.architector.port.adapter.project.file.FileMapper.mapToModel;

/**
 * @author Valeriy Knyazhev
 */
@RestController
public class FileResource {

    private final IFCFileReader fileReader;

    private final ProjectRepository projectRepository;

    public FileResource(@Nonnull IFCFileReader fileReader,
                        @Nonnull ProjectRepository projectRepository) {
        this.fileReader = Args.notNull(fileReader, "File reader is required.");
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
    }

    @GetMapping(value = "/projects/{qProjectId}/files/{qFileId}",
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> findFile(@PathVariable String qProjectId,
                                           @PathVariable String qFileId) {
        Args.notNull(qProjectId, "Project identifier is required.");
        Args.notNull(qFileId, "File identifier is required.");
        ProjectId projectId = ProjectId.of(qProjectId);
        FileId fileId = FileId.of(qFileId);
        Project project = this.projectRepository.findByProjectId(projectId)
                .orElse(null);
        if (project == null) {
            return ResponseEntity.status(NOT_FOUND).body(new ResponseMessage()
                    .error("Project with identifier " + qProjectId + " not found."));
        }
        File foundFile = project.files().stream()
                .filter(file -> fileId.equals(file.fileId()))
                .findFirst()
                .orElse(null);
        if (foundFile == null) {
            return ResponseEntity.status(NOT_FOUND).body(new ResponseMessage()
                    .error("File with identifier " + qFileId + " in project [" + qProjectId + "] not found."));
        }
        return ResponseEntity.ok(mapToModel(foundFile));
    }

    @PostMapping(value = "/projects/{qProjectId}/files",
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> addFileFromUrl(@PathVariable String qProjectId,
                                                 @RequestBody AddFileCommand command) {
        Args.notNull(qProjectId, "Project identifier is required.");
        Args.notNull(command, "Add file command is required.");
        try {
            Project project = this.projectRepository.findByProjectId(ProjectId.of(qProjectId))
                    .orElse(null);
            if (project == null) {
                return ResponseEntity.badRequest().body(new ResponseMessage()
                        .error("Project with identifier " + qProjectId + " not found."));
            }
            URL fileUrl = new URL(command.fileUrl());
            File file = this.fileReader.readFileFromUrl(fileUrl);
            project.addFile(file);
            projectRepository.save(project);
            return ResponseEntity.ok().body(new ResponseMessage()
                    .info("File " + file.fileId().id() + " was added to project " + qProjectId));
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage().error(e.getMessage()));
        }
    }

    @DeleteMapping(value = "/projects/{qProjectId}/files/{qFileId}",
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> removeFile(@PathVariable String qProjectId,
                                             @PathVariable String qFileId) {
        Args.notNull(qProjectId, "Project identifier is required.");
        Args.notNull(qFileId, "File identifier is required.");
        ProjectId projectId = ProjectId.of(qProjectId);
        FileId fileId = FileId.of(qFileId);
        Project project = this.projectRepository.findByProjectId(projectId)
                .orElse(null);
        if (project == null) {
            return ResponseEntity.status(NOT_FOUND).body(new ResponseMessage()
                    .error("Project with identifier " + qProjectId + " not found."));
        }
        project.files().stream()
                .filter(file -> fileId.equals(file.fileId()))
                .findFirst()
                .ifPresent(project::removeFile);
        this.projectRepository.save(project);
        return ResponseEntity.ok(new ResponseMessage()
                .info("File with identifier " + qFileId + " was deleted from project " + qProjectId));
    }

}
