package valeriy.knyazhev.architector.port.adapter.resources.project.file.conflict;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import valeriy.knyazhev.architector.application.project.file.conflict.ResolveChangesConflictService;
import valeriy.knyazhev.architector.application.project.file.conflict.command.ResolveContentConflictCommand;
import valeriy.knyazhev.architector.application.project.file.conflict.command.ResolveDescriptionConflictCommand;
import valeriy.knyazhev.architector.application.project.file.conflict.command.ResolveMetadataConflictCommand;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.request.UpdateFileContentRequest;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.request.UpdateFileDescriptionRequest;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.request.UpdateFileMetadataRequest;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author Valeriy Knyazhev
 */
@RestController
public class FileResolveConflictResource
{

    private final ResolveChangesConflictService resolveConflictService;

    public FileResolveConflictResource(@Nonnull ResolveChangesConflictService resolveConflictService)
    {
        this.resolveConflictService = Args.notNull(resolveConflictService,
            "Resolve changes conflict service is required.");
    }

    @PostMapping(value = "/api/projects/{qProjectId}/files/{qFileId}/content/resolve-conflict",
                 consumes = APPLICATION_JSON_UTF8_VALUE,
                 produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> resolveDescriptionConflict(@PathVariable String qProjectId,
                                                             @PathVariable String qFileId,
                                                             @RequestBody UpdateFileContentRequest request,
                                                             @Nonnull Architector architector)
    {
        boolean resolved = this.resolveConflictService.resolveContentChangesConflict(
            ResolveContentConflictCommand.builder()
                .projectId(qProjectId)
                .fileId(qFileId)
                .architector(architector)
                .headCommitId(request.headCommitId())
                .content(request.content())
                .build()
        );
        return resolved
               ? ResponseEntity.ok()
                   .body(
                       new ResponseMessage().info("File " + qFileId + " content conflict was resolved.")
                   )
               : ResponseEntity.badRequest()
                   .body(
                       new ResponseMessage().error("Something went wrong: conflict not resolved.")
                   );
    }

    @PostMapping(value = "/api/projects/{qProjectId}/files/{qFileId}/description/resolve-conflict",
                 consumes = APPLICATION_JSON_UTF8_VALUE,
                 produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> resolveDescriptionConflict(@PathVariable String qProjectId,
                                                             @PathVariable String qFileId,
                                                             @RequestBody UpdateFileDescriptionRequest request,
                                                             @Nonnull Architector architector)
    {
        boolean resolved = this.resolveConflictService.resolveDescriptionChangesConflict(
            ResolveDescriptionConflictCommand.builder()
                .projectId(qProjectId)
                .fileId(qFileId)
                .architector(architector)
                .headCommitId(request.headCommitId())
                .descriptions(request.descriptions())
                .implementationLevel(request.implementationLevel())
                .build()
        );
        return resolved
               ? ResponseEntity.ok()
                   .body(
                       new ResponseMessage().info("File " + qFileId + " description conflict was resolved.")
                   )
               : ResponseEntity.badRequest()
                   .body(
                       new ResponseMessage().error("Something went wrong: conflict not resolved.")
                   );
    }

    @PostMapping(value = "/api/projects/{qProjectId}/files/{qFileId}/metadata/resolve-conflict",
                 consumes = APPLICATION_JSON_UTF8_VALUE,
                 produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> resolveMetadataConflict(@PathVariable String qProjectId,
                                                          @PathVariable String qFileId,
                                                          @RequestBody UpdateFileMetadataRequest request,
                                                          @Nonnull Architector architector)
    {
        boolean resolved = this.resolveConflictService.resolveMetadataChangesConflict(
            ResolveMetadataConflictCommand.builder()
                .projectId(qProjectId)
                .fileId(qFileId)
                .architector(architector)
                .headCommitId(request.headCommitId())
                .name(request.name())
                .timestamp(request.timestamp())
                .authors(request.authors())
                .organizations(request.organizations())
                .preprocessorVersion(request.preprocessorVersion())
                .originatingSystem(request.originatingSystem())
                .authorization(request.authorization())
                .build()
        );
        return resolved
               ? ResponseEntity.ok()
                   .body(
                       new ResponseMessage().info("File " + qFileId + " metadata conflict was resolved.")
                   )
               : ResponseEntity.badRequest()
                   .body(
                       new ResponseMessage().error("Something went wrong: conflict not resolved.")
                   );
    }

}
