package valeriy.knyazhev.architector.port.adapter.resources.project.file;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.FileManagementService;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.application.project.file.command.AddFileFromUploadCommand;
import valeriy.knyazhev.architector.application.project.file.command.AddFileFromUrlCommand;
import valeriy.knyazhev.architector.application.project.file.command.UpdateFileFromUploadCommand;
import valeriy.knyazhev.architector.application.project.file.command.UpdateFileFromUrlCommand;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.request.CreateFileFromUploadRequest;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.request.CreateFileFromUrlRequest;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.request.UpdateFileFromUploadRequest;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.request.UpdateFileFromUrlRequest;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static valeriy.knyazhev.architector.port.adapter.resources.project.file.model.FileMapper.buildContent;
import static valeriy.knyazhev.architector.port.adapter.resources.project.file.model.FileMapper.buildFile;

/**
 * @author Valeriy Knyazhev
 */
@RestController
public class FileResource {

    private final ProjectRepository projectRepository;

    private final FileManagementService managementService;

    public FileResource(@Nonnull ProjectRepository projectRepository,
                        @Nonnull FileManagementService managementService) {
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
        this.managementService = Args.notNull(managementService, "File management service is required.");
    }

    @GetMapping(value = "/api/projects/{qProjectId}/files/{qFileId}",
        produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> findFile(@PathVariable String qProjectId,
                                           @PathVariable String qFileId) {
        Args.notNull(qProjectId, "Project identifier is required.");
        Args.notNull(qFileId, "File identifier is required.");
        ProjectId projectId = ProjectId.of(qProjectId);
        FileId fileId = FileId.of(qFileId);
        File foundFile = fetchFile(projectId, fileId);
        return ResponseEntity.ok(buildFile(foundFile));
    }

    @GetMapping(value = "/api/projects/{qProjectId}/files/{qFileId}/content",
        produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchFileContent(@PathVariable String qProjectId,
                                                   @PathVariable String qFileId) {
        Args.notNull(qProjectId, "Project identifier is required.");
        Args.notNull(qFileId, "File identifier is required.");
        ProjectId projectId = ProjectId.of(qProjectId);
        FileId fileId = FileId.of(qFileId);
        File foundFile = fetchFile(projectId, fileId);
        return ResponseEntity.ok(buildContent(foundFile));
    }

    @PostMapping(value = "/api/projects/{qProjectId}/files/source",
        consumes = APPLICATION_JSON_UTF8_VALUE,
        produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> addFileFromUrl(@PathVariable String qProjectId,
                                                 @RequestBody CreateFileFromUrlRequest request) {
        Args.notNull(qProjectId, "Project identifier is required.");
        Args.notNull(request, "Add file from url request is required.");
        File newFile = this.managementService.addFile(new AddFileFromUrlCommand(
            qProjectId, "author", request.name(), request.sourceUrl()
            )
        );
        return newFile != null
            ? ResponseEntity.ok().body(
            new ResponseMessage().info("File " + newFile.fileId().id() + " was added to project " + qProjectId))
            : ResponseEntity.badRequest().body(
            new ResponseMessage().error("Unable to add file to project " + qProjectId + " from source URL."));
    }

    @PostMapping(value = "/api/projects/{qProjectId}/files/import",
        consumes = MULTIPART_FORM_DATA_VALUE,
        produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseMessage> addFileFromFile(@PathVariable String qProjectId,
                                                           @RequestBody CreateFileFromUploadRequest request,
                                                           @RequestParam("file") MultipartFile multipartFile) {
        Args.notNull(qProjectId, "Project identifier is required.");
        Args.notNull(multipartFile, "Upload file is required.");
        File newFile = this.managementService.addFile(new AddFileFromUploadCommand(
            qProjectId, "author", request.name(), multipartFile
            )
        );
        return newFile != null
            ? ResponseEntity.ok().body(
            new ResponseMessage().info("File " + newFile.fileId().id() + " was added to project " + qProjectId))
            : ResponseEntity.badRequest().body(
            new ResponseMessage().error("Unable to add file to project " + qProjectId + " from upload file."));

    }

    @PutMapping(value = "/api/projects/{qProjectId}/files/{qFileId}/source",
        consumes = APPLICATION_JSON_UTF8_VALUE,
        produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> updateFromUrl(@PathVariable String qProjectId,
                                                @PathVariable String qFileId,
                                                @RequestBody UpdateFileFromUrlRequest request) {
        boolean updated = this.managementService.updateFile(
            // TODO author constant should be replaced by user email
            new UpdateFileFromUrlCommand(
                qProjectId, qFileId, "author", request.message(), request.sourceUrl()
            )
        );
        return updated
            ? ResponseEntity.ok().body(
            new ResponseMessage().info("Project " + qProjectId + " was updated from source URL."))
            : ResponseEntity.badRequest().body(
            new ResponseMessage().error("Unable to update project " + qProjectId + " from source URL."));
    }

    @PutMapping(value = "/api/projects/{qProjectId}/files/{qFileId}/import",
        consumes = MULTIPART_FORM_DATA_VALUE,
        produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseMessage> updateFromFile(@PathVariable String qProjectId,
                                                          @PathVariable String qFileId,
                                                          @RequestBody UpdateFileFromUploadRequest request,
                                                          @RequestParam("file") MultipartFile multipartFile) {
        boolean updated = this.managementService.updateFile(
            // TODO author constant should be replaced by user email
            new UpdateFileFromUploadCommand(
                qProjectId, qFileId, "author", request.message(), multipartFile
            )
        );
        return updated
            ? ResponseEntity.ok().body(
            new ResponseMessage().info("Project " + qProjectId + " was updated from received file."))
            : ResponseEntity.badRequest().body(
            new ResponseMessage().error("Unable to update project " + qProjectId + " from received file."));
    }

    @Nonnull
    private File fetchFile(@Nonnull ProjectId projectId, @Nonnull FileId fileId) {
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        return project.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId));
    }

}
