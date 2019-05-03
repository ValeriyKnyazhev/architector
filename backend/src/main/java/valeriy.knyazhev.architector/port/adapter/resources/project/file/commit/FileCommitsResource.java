package valeriy.knyazhev.architector.port.adapter.resources.project.file.commit;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.commit.Commit;
import valeriy.knyazhev.architector.domain.model.project.commit.CommitRepository;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.commit.model.FileCommitBriefModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.commit.model.FileCommitsModel;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RestController
public class FileCommitsResource
{

    private final CommitRepository commitRepository;

    private final ProjectRepository projectRepository;

    public FileCommitsResource(@Nonnull CommitRepository commitRepository,
                               @Nonnull ProjectRepository projectRepository)
    {
        this.commitRepository = Args.notNull(commitRepository, "Commit repository is required.");
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
    }

    @Nonnull
    private static FileCommitBriefModel constructBriefDescription(@Nonnull Commit commit)
    {
        return new FileCommitBriefModel(
            commit.id(),
            commit.parentId(),
            commit.author(),
            commit.message(),
            commit.timestamp()
        );
    }

    private static boolean commitRelatedToFile(@Nonnull Commit commit, @Nonnull FileId fileId)
    {
        return commit.data()
            .changedFiles()
            .stream()
            .anyMatch(file -> fileId.equals(file.fileId()));
    }

    @GetMapping(value = "api/projects/{qProjectId}/files/{qFileId}/commits",
        produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> fetchFileChanges(@PathVariable String qProjectId,
                                                   @PathVariable String qFileId)
    {
        ProjectId projectId = ProjectId.of(qProjectId);
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        FileId fileId = FileId.of(qFileId);
        List<FileCommitBriefModel> commits = this.commitRepository
            .findByProjectIdOrderByIdDesc(projectId)
            .stream()
            .filter(commit -> commitRelatedToFile(commit, fileId))
            .map(FileCommitsResource::constructBriefDescription)
            .collect(toList());
        return ResponseEntity.ok(
            new FileCommitsModel(
                projectId.id(),
                fileId.id(),
                project.name(),
                commits
            )
        );
    }

}
