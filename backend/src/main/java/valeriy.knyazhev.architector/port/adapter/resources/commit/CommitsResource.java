package valeriy.knyazhev.architector.port.adapter.resources.commit;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import valeriy.knyazhev.architector.application.commit.CommitApplicationService;
import valeriy.knyazhev.architector.application.commit.command.FindCommitsCommand;
import valeriy.knyazhev.architector.application.commit.command.MakeFileProjectionCommand;
import valeriy.knyazhev.architector.application.commit.command.MakeProjectProjectionCommand;
import valeriy.knyazhev.architector.application.commit.data.FileHistoryData;
import valeriy.knyazhev.architector.application.commit.data.ProjectHistoryData;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.DescriptionModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.FileContentModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.MetadataModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.model.ProjectContentModel;

import javax.annotation.Nonnull;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RestController
public class CommitsResource
{

    private final CommitApplicationService applicationService;

    private final ProjectRepository projectRepository;

    public CommitsResource(@Nonnull CommitApplicationService applicationService,
                           @Nonnull ProjectRepository projectRepository)
    {
        this.applicationService = Args.notNull(applicationService, "Commit application service is required.");
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
    }

    @GetMapping(value = "api/projects/{qProjectId}/commits",
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchProjectChanges(@PathVariable String qProjectId)
    {
        ProjectHistoryData projectHistory = (ProjectHistoryData) this.applicationService.fetchProjectHistory(
            FindCommitsCommand.builder()
                .projectId(qProjectId)
                .build()
        );
        return ResponseEntity.ok(projectHistory);
    }

    @GetMapping(value = "api/projects/{qProjectId}/files/{qFileId}/commits",
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchFileChanges(@PathVariable String qProjectId,
                                                   @PathVariable String qFileId)
    {
        FileHistoryData fileHistory = (FileHistoryData) this.applicationService.fetchProjectHistory(
            FindCommitsCommand.builder()
                .projectId(qProjectId)
                .fileId(qFileId)
                .build()
        );
        return ResponseEntity.ok(fileHistory);
    }

    @GetMapping(value = "api/projects/{qProjectId}/commits/{commitId}/content",
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchProjectContentByCommit(
        @PathVariable String qProjectId,
        @PathVariable long commitId)
    {
        Projection projection = this.applicationService.makeProjection(
            new MakeProjectProjectionCommand(qProjectId, commitId)
        );
        return ResponseEntity.ok(
            new ProjectContentModel(
                qProjectId,
                projection.name(),
                projection.description(),
                projection.files().stream()
                    .filter(file -> !file.items().isEmpty())
                    .map(CommitsResource::constructFileContent)
                    .collect(toList())
            )
        );
    }

    @GetMapping(value = "api/projects/{qProjectId}/files/{qFileId}/commits/{commitId}/content",
                produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchFileContentByCommit(
        @PathVariable String qProjectId,
        @PathVariable String qFileId,
        @PathVariable long commitId)
    {
        Projection.FileProjection projection = this.applicationService.makeProjection(
            new MakeFileProjectionCommand(qProjectId, qFileId, commitId)
        );
        return ResponseEntity.ok(
            constructFileContent(projection)
        );
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
