package valeriy.knyazhev.architector.port.adapter.project.file;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static valeriy.knyazhev.architector.port.adapter.project.file.FileMapper.mapToModel;

/**
 * @author Valeriy Knyazhev
 */
@RestController
public class FileResource {

    private final ProjectRepository projectRepository;

    public FileResource(@Nonnull ProjectRepository projectRepository) {
        this.projectRepository = Args.notNull(projectRepository, "Project repository");
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
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ResponseMessage().error("Project with identifier " + qProjectId + " not found."));
        }
        File foundFile = project.files().stream()
                .filter(file -> fileId.equals(file.fileId()))
                .findFirst()
                .orElse(null);
        if (foundFile == null) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ResponseMessage().error("File with identifier " + qFileId + " in project ["
                            + qProjectId + "] not found."));
        }
        return ResponseEntity.ok(mapToModel(foundFile));
    }

}
