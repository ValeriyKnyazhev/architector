package valeriy.knyazhev.architector.application.commit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.*;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.factory.CommitObjectFactory;
import valeriy.knyazhev.architector.factory.ProjectObjectFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static valeriy.knyazhev.architector.factory.CommitObjectFactory.commitWithContentChanges;
import static valeriy.knyazhev.architector.factory.CommitObjectFactory.commitWithData;

/**
 * @author Valeriy Knyazhev
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProjectionConstructService.class)
public class ProjectionConstructServiceTests
{

    @Autowired
    private ProjectionConstructService projectionConstructService;

    @MockBean
    private CommitRepository commitRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @Test
    public void shouldMakeProjection()
    {
        // given
        long commitId = 2L;
        Project project = ProjectObjectFactory.emptyProject("author");
        ProjectId projectId = project.projectId();
        List<Commit> commits = List.of(initCommit(projectId, FileId.nextId(), commitId));
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));
        when(this.commitRepository.findByProjectIdOrderById(projectId))
            .thenReturn(commits);

        // when
        Projection result = this.projectionConstructService.makeProjection(projectId, commitId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(project.name());
        assertThat(result.description()).isEqualTo(project.description());
        assertThat(result.files()).isNotEmpty();
    }

    @Test
    public void shouldMakeProjectionOfNewProject()
    {
        // given
        Project project = ProjectObjectFactory.emptyProject("author");
        ProjectId projectId = project.projectId();
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // when
        Projection result = this.projectionConstructService.makeProjection(projectId, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(project.name());
        assertThat(result.description()).isEqualTo(project.description());
        assertThat(result.files()).isEmpty();
    }

    @Test
    public void shouldNotMakeProjectionIfProjectNotFound()
    {
        // given
        long commitId = 2L;
        ProjectId projectId = ProjectId.nextId();
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() ->this.projectionConstructService.makeProjection(projectId, commitId))
            .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    public void shouldNotMakeProjectionIfCommitsNotValid()
    {
        // given
        long rootCommitId = 1L;
        long commitId = 2L;
        long parentCommitId = 3L;
        Project project = ProjectObjectFactory.emptyProject("author");
        ProjectId projectId = project.projectId();
        List<CommitItem> items = List.of(CommitItem.addItem("value", 0));
        List<Commit> commits = List.of(
            initCommit(projectId, FileId.nextId(), rootCommitId),
            commitWithContentChanges(
                projectId, FileId.nextId(), commitId, parentCommitId, items
            )
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));
        when(this.commitRepository.findByProjectIdOrderById(projectId))
            .thenReturn(commits);

        // expect
        assertThatThrownBy(() -> this.projectionConstructService.makeProjection(projectId, commitId))
            .isExactlyInstanceOf(CommitNotFoundException.class);
    }

    private static Commit initCommit(ProjectId projectId, FileId fileId, long commitId)
    {
        FileMetadataChanges metadataChanges = FileMetadataChanges.builder()
            .name("")
            .authors(List.of())
            .organizations(List.of())
            .timestamp(LocalDateTime.now())
            .preprocessorVersion("")
            .originatingSystem("")
            .authorization("")
            .build();
        FileDescriptionChanges descriptionChanges = FileDescriptionChanges.builder()
            .descriptions(List.of())
            .implementationLevel("")
            .build();
        List<CommitItem> items = List.of(CommitItem.addItem("value", 0));
        return commitWithData(
            projectId, fileId, commitId, null, metadataChanges, descriptionChanges, items
        );
    }

}