package valeriy.knyazhev.architector.port.adapter.resources.commit;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import valeriy.knyazhev.architector.application.commit.ChangesApplicationService;
import valeriy.knyazhev.architector.application.commit.CommitQueryService;
import valeriy.knyazhev.architector.application.commit.command.FetchChangesHistoryCommand;
import valeriy.knyazhev.architector.application.commit.command.FetchCommitChangesCommand;
import valeriy.knyazhev.architector.application.commit.command.MakeFileProjectionCommand;
import valeriy.knyazhev.architector.application.commit.command.MakeProjectProjectionCommand;
import valeriy.knyazhev.architector.application.commit.data.changes.CommitChangesData;
import valeriy.knyazhev.architector.application.commit.data.history.FileHistoryData;
import valeriy.knyazhev.architector.application.commit.data.history.ProjectHistoryData;
import valeriy.knyazhev.architector.application.project.file.IFCFileWriter;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.DescriptionModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.FileContentModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.MetadataModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.model.ProjectContentModel;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RestController
public class CommitResource
{

    private final CommitQueryService commitQueryService;

    private final ChangesApplicationService changesApplicationService;

    public CommitResource(@Nonnull CommitQueryService commitQueryService,
                          @Nonnull ChangesApplicationService changesApplicationService)
    {
        this.commitQueryService = Args.notNull(commitQueryService, "Commit query service is required.");
        this.changesApplicationService = Args.notNull(changesApplicationService,
            "Changes application service is required.");
    }

    @GetMapping(value = "/api/projects/{qProjectId}/commits",
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchProjectChanges(@PathVariable String qProjectId)
    {
        ProjectHistoryData projectHistory = (ProjectHistoryData) this.commitQueryService.fetchProjectHistory(
            FetchChangesHistoryCommand.builder()
                .projectId(qProjectId)
                .build()
        );
        return ResponseEntity.ok(projectHistory);
    }

    @GetMapping(value = "/api/projects/{qProjectId}/files/{qFileId}/commits",
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchFileChanges(@PathVariable String qProjectId,
                                                   @PathVariable String qFileId)
    {
        FileHistoryData fileHistory = (FileHistoryData) this.commitQueryService.fetchProjectHistory(
            FetchChangesHistoryCommand.builder()
                .projectId(qProjectId)
                .fileId(qFileId)
                .build()
        );
        return ResponseEntity.ok(fileHistory);
    }

    @GetMapping(value = "/api/projects/{qProjectId}/commits/{commitId}/content",
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchProjectContentByCommit(@PathVariable String qProjectId,
                                                              @PathVariable long commitId)
    {
        Projection projection = this.commitQueryService.fetchProjection(
            new MakeProjectProjectionCommand(qProjectId, commitId)
        );
        return ResponseEntity.ok(
            new ProjectContentModel(
                qProjectId,
                projection.name(),
                projection.description(),
                projection.files().stream()
                    .filter(file -> !file.items().isEmpty())
                    .map(CommitResource::constructFileContent)
                    .collect(toList())
            )
        );
    }

    @GetMapping(value = "/api/projects/{qProjectId}/files/{qFileId}/commits/{commitId}/content",
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchFileContentByCommit(
        @PathVariable String qProjectId,
        @PathVariable String qFileId,
        @PathVariable long commitId)
    {
        Projection.FileProjection projection = this.commitQueryService.fetchProjection(
            new MakeFileProjectionCommand(qProjectId, qFileId, commitId)
        );
        return ResponseEntity.ok(
            constructFileContent(projection)
        );
    }

    @GetMapping(value = "/api/projects/{qProjectId}/files/{qFileId}/commits/{commitId}/download",
                produces = APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] downloadFile(@PathVariable String qProjectId,
                                             @PathVariable String qFileId,
                                             @PathVariable long commitId,
                                             HttpServletResponse response)
    {
        Args.notNull(qProjectId, "Project identifier is required.");
        Args.notNull(qFileId, "File identifier is required.");
        Projection.FileProjection projection = this.commitQueryService.fetchProjection(
            new MakeFileProjectionCommand(qProjectId, qFileId, commitId)
        );
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=\"" + projection.metadata().name() + ".ifc\""
        );
        return IFCFileWriter.write(projection);
    }

    @GetMapping(value = "/api/projects/{qProjectId}/commits/{commitId}/changes",
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchCommitChanges(@PathVariable String qProjectId,
                                                     @PathVariable long commitId)
    {
        CommitChangesData changes = this.changesApplicationService.fetchCommitChanges(
            new FetchCommitChangesCommand(qProjectId, commitId)
        );
        return ResponseEntity.ok(changes);
    }

    @Nonnull
    private static FileContentModel constructFileContent(@Nonnull Projection.FileProjection file)
    {
        return new FileContentModel(
            file.fileId().id(),
            MetadataModel.of(file.metadata()),
            DescriptionModel.of(file.description()),
            file.items()
        );
    }

}
