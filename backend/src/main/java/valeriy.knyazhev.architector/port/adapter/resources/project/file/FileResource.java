package valeriy.knyazhev.architector.port.adapter.resources.project.file;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.FileManagementService;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.application.project.file.IFCFileWriter;
import valeriy.knyazhev.architector.application.project.file.command.*;
import valeriy.knyazhev.architector.application.project.file.conflict.exception.FileContentConflictException;
import valeriy.knyazhev.architector.application.project.file.conflict.exception.FileDescriptionConflictException;
import valeriy.knyazhev.architector.application.project.file.conflict.exception.FileMetadataConflictException;
import valeriy.knyazhev.architector.application.project.file.validation.ChangedEntity;
import valeriy.knyazhev.architector.domain.model.AccessRightsNotFoundException;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.conflict.model.FileContentConflictModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.conflict.model.FileDescriptionConflictModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.conflict.model.FileMetadataConflictModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.request.CreateFileFromUrlRequest;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.request.UpdateFileContentRequest;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.request.UpdateFileDescriptionRequest;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.request.UpdateFileMetadataRequest;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.*;
import static valeriy.knyazhev.architector.port.adapter.resources.project.file.model.FileMapper.buildFile;

/**
 * @author Valeriy Knyazhev
 */
@RestController
public class FileResource
{

    private final ProjectRepository projectRepository;

    private final FileManagementService managementService;

    public FileResource(@Nonnull ProjectRepository projectRepository,
                        @Nonnull FileManagementService managementService)
    {
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
        this.managementService = Args.notNull(managementService, "File management service is required.");
    }

    @GetMapping(value = "/api/projects/{qProjectId}/files/{qFileId}",
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> findFile(@PathVariable String qProjectId,
                                           @PathVariable String qFileId,
                                           @Nonnull Architector architector)
    {
        Args.notNull(qProjectId, "Project identifier is required.");
        Args.notNull(qFileId, "File identifier is required.");
        Project project = findProject(ProjectId.of(qProjectId), architector);
        File foundFile = fetchFile(project, FileId.of(qFileId));
        // Project current commit id exist
        return ResponseEntity.ok(
            buildFile(foundFile, project.accessRights(architector), project.currentCommitId())
        );
    }

    @GetMapping(value = "/api/projects/{qProjectId}/files/{qFileId}/download",
                produces = APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] downloadFile(@PathVariable String qProjectId,
                                             @PathVariable String qFileId,
                                             @Nonnull Architector architector,
                                             HttpServletResponse response)
    {
        Args.notNull(qProjectId, "Project identifier is required.");
        Args.notNull(qFileId, "File identifier is required.");
        Project project = findProject(ProjectId.of(qProjectId), architector);
        File foundFile = fetchFile(project, FileId.of(qFileId));
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=\"" + foundFile.metadata().name() + ".ifc\""
        );
        return IFCFileWriter.write(foundFile);
    }

    @PostMapping(value = "/api/projects/{qProjectId}/files/source",
                 consumes = APPLICATION_JSON_UTF8_VALUE,
                 produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> addFileFromUrl(@PathVariable String qProjectId,
                                                 @RequestBody CreateFileFromUrlRequest request,
                                                 @Nonnull Architector architector)
    {
        Args.notNull(qProjectId, "Project identifier is required.");
        Args.notNull(request, "Add file from url request is required.");
        File newFile = this.managementService.addFile(
            new AddFileFromUrlCommand(
                qProjectId, architector, request.sourceUrl()
            )
        );
        return newFile != null
               ? ResponseEntity.ok()
                   .body(
                       new ResponseMessage()
                           .info("File " + newFile.fileId().id() + " was added to project " + qProjectId)
                   )
               : ResponseEntity.badRequest()
                   .body(
                       new ResponseMessage().error("Unable to add file to project " + qProjectId + " from source URL.")
                   );
    }

    @PostMapping(value = "/api/projects/{qProjectId}/files/import",
                 consumes = MULTIPART_FORM_DATA_VALUE,
                 produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseMessage> addFileFromFile(@PathVariable String qProjectId,
                                                           @RequestParam("file") MultipartFile multipartFile,
                                                           @Nonnull Architector architector)
    {
        Args.notNull(qProjectId, "Project identifier is required.");
        Args.notNull(multipartFile, "Upload file is required.");
        File newFile = this.managementService.addFile(
            new AddFileFromUploadCommand(
                qProjectId, architector, multipartFile
            )
        );
        return newFile != null
               ? ResponseEntity.ok()
                   .body(
                       new ResponseMessage()
                           .info("File " + newFile.fileId().id() + " was added to project " + qProjectId)
                   )
               : ResponseEntity.badRequest()
                   .body(
                       new ResponseMessage().error("Unable to add file to project " + qProjectId + " from upload file.")
                   );

    }

    @PutMapping(value = "/api/projects/{qProjectId}/files/{qFileId}/content",
                consumes = APPLICATION_JSON_UTF8_VALUE,
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> updateFromUrl(@PathVariable String qProjectId,
                                                @PathVariable String qFileId,
                                                @RequestBody UpdateFileContentRequest request,
                                                @Nonnull Architector architector)
    {
        List<ChangedEntity> changedRootEntities = List.of();
        try
        {
            changedRootEntities = this.managementService.updateFileContent(
                new UpdateFileContentCommand(
                    qProjectId, qFileId, architector, request.content(), request.commitMessage(), request.headCommitId()
                )
            );
        } catch (FileContentConflictException ex)
        {
            return ResponseEntity.ok(
                new FileContentConflictModel(
                    ex.oldContent(),
                    ex.conflictBlocks(),
                    ex.headCommitId(),
                    FileContentConflictModel.Links.of(qProjectId, qFileId)
                )
            );
        }
        return ResponseEntity.ok().body(Map.of("updatedRoots", changedRootEntities));
    }

    @PutMapping(value = "/api/projects/{qProjectId}/files/{qFileId}/description",
                consumes = APPLICATION_JSON_UTF8_VALUE,
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> updateFileDescription(@PathVariable String qProjectId,
                                                        @PathVariable String qFileId,
                                                        @RequestBody UpdateFileDescriptionRequest request,
                                                        @Nonnull Architector architector)
    {
        boolean updated = false;
        try
        {
            updated = this.managementService.updateFileDescription(
                UpdateFileDescriptionCommand.builder()
                    .projectId(qProjectId)
                    .fileId(qFileId)
                    .architector(architector)
                    .descriptions(request.descriptions())
                    .implementationLevel(request.implementationLevel())
                    .headCommitId(request.headCommitId())
                    .build()
            );
        } catch (FileDescriptionConflictException ex)
        {
            return ResponseEntity.ok(
                new FileDescriptionConflictModel(
                    ex.changes(),
                    ex.headCommitId(),
                    FileDescriptionConflictModel.Links.of(qProjectId, qFileId)
                )
            );
        }
        return updated
               ? ResponseEntity.ok()
                   .body(
                       new ResponseMessage().info("File " + qFileId + " description was updated.")
                   )
               : ResponseEntity.badRequest()
                   .body(
                       new ResponseMessage().error("Unable to update file " + qFileId + " description.")
                   );
    }

    @PutMapping(value = "/api/projects/{qProjectId}/files/{qFileId}/metadata",
                consumes = APPLICATION_JSON_UTF8_VALUE,
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> updateFileMetadata(@PathVariable String qProjectId,
                                                     @PathVariable String qFileId,
                                                     @RequestBody UpdateFileMetadataRequest request,
                                                     @Nonnull Architector architector)
    {
        boolean updated = false;
        try
        {
            updated = this.managementService.updateFileMetadata(
                UpdateFileMetadataCommand.builder()
                    .projectId(qProjectId)
                    .fileId(qFileId)
                    .architector(architector)
                    .name(request.name())
                    .timestamp(request.timestamp())
                    .authors(request.authors())
                    .organizations(request.organizations())
                    .preprocessorVersion(request.preprocessorVersion())
                    .originatingSystem(request.originatingSystem())
                    .authorization(request.authorization())
                    .headCommitId(request.headCommitId())
                    .build()
            );
        } catch (FileMetadataConflictException ex)
        {
            return ResponseEntity.ok(
                new FileMetadataConflictModel(
                    ex.changes(),
                    ex.headCommitId(),
                    FileMetadataConflictModel.Links.of(qProjectId, qFileId)
                )
            );
        }
        return updated
               ? ResponseEntity.ok()
                   .body(
                       new ResponseMessage().info("File " + qFileId + " metadata was updated.")
                   )
               : ResponseEntity.badRequest()
                   .body(
                       new ResponseMessage().error("Unable to update file " + qFileId + " metadata.")
                   );
    }

    @DeleteMapping(value = "/api/projects/{qProjectId}/files/{qFileId}",
                   produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseMessage> updateFromFile(@PathVariable String qProjectId,
                                                          @PathVariable String qFileId,
                                                          @Nonnull Architector architector)
    {
        boolean deleted = this.managementService.deleteFile(
            new DeleteFileCommand(qProjectId, qFileId, architector)
        );
        return deleted
               ? ResponseEntity.ok()
                   .body(
                       new ResponseMessage().info("File " + qFileId + " was deleted from project.")
                   )
               : ResponseEntity.badRequest()
                   .body(
                       new ResponseMessage().error("Unable to delete file " + qFileId + " from project.")
                   );
    }

    @Nonnull
    private Project findProject(@Nonnull ProjectId projectId, @Nonnull Architector architector)
    {
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        if (!project.canBeRead(architector))
        {
            throw new AccessRightsNotFoundException();
        }
        return project;
    }

    @Nonnull
    private File fetchFile(@Nonnull Project project, @Nonnull FileId fileId)
    {
        return project.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(project.projectId(), fileId));
    }

}
