package valeriy.knyazhev.architector.application.commit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.application.commit.command.FetchCommitChangesCommand;
import valeriy.knyazhev.architector.application.commit.data.changes.CommitChangesData;
import valeriy.knyazhev.architector.domain.model.commit.*;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

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
@SpringBootTest(classes = ChangesApplicationService.class)
public class ChangesApplicationServiceTests
{

    @Autowired
    private ChangesApplicationService changesApplicationService;

    @MockBean
    private CommitRepository commitRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private ProjectionConstructService projectionConstructService;

    @Test
    public void shouldFetchCommitChangesIfFileExist()
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        long commitId = 2L;
        long commitParentId = 1L;
        String projectName = "project";
        String projectDescription = "description";
        FetchCommitChangesCommand command = new FetchCommitChangesCommand(projectId.id(), commitId);
        when(this.commitRepository.findById(commitId))
            .thenReturn(Optional.of(sampleCommit(projectId, fileId, commitId, commitParentId)));
        when(this.projectionConstructService.makeProjection(projectId, commitParentId))
            .thenReturn(Projection.of(
                "project", "description", List.of(sampleFileProjection(fileId))
                )
            );

        // when
        CommitChangesData result = this.changesApplicationService.fetchCommitChanges(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(projectName);
        assertThat(result.description()).isEqualTo(projectDescription);
        assertThat(result.changedFiles()).isNotEmpty();
    }

    @Test
    public void shouldFetchCommitChangesIfFileNotExist()
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        long commitId = 2L;
        long commitParentId = 1L;
        String projectName = "project";
        String projectDescription = "description";
        FetchCommitChangesCommand command = new FetchCommitChangesCommand(projectId.id(), commitId);
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
        Commit commit = commitWithData(
            projectId, fileId, commitId, commitParentId, metadataChanges, descriptionChanges, items
        );
        when(this.commitRepository.findById(commitId))
            .thenReturn(Optional.of(commit));
        when(this.projectionConstructService.makeProjection(projectId, commitParentId))
            .thenReturn(Projection.of(
                "project", "description", List.of()
                )
            );

        // when
        CommitChangesData result = this.changesApplicationService.fetchCommitChanges(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(projectName);
        assertThat(result.description()).isEqualTo(projectDescription);
        assertThat(result.changedFiles()).isNotEmpty();
    }

    @Test
    public void shouldNotFetchCommitChangesIfCommitNotFound()
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        long commitId = 2L;
        FetchCommitChangesCommand command = new FetchCommitChangesCommand(projectId.id(), commitId);
        when(this.commitRepository.findById(commitId))
            .thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> this.changesApplicationService.fetchCommitChanges(command))
            .isExactlyInstanceOf(CommitNotFoundException.class);
    }

    @Test
    public void shouldNotFetchCommitChangesIfCommitNotFromRequestedProject()
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        long commitId = 2L;
        FetchCommitChangesCommand command = new FetchCommitChangesCommand(projectId.id(), commitId);
        when(this.commitRepository.findById(commitId))
            .thenReturn(Optional.of(sampleCommit(ProjectId.nextId(), FileId.nextId(), commitId, null)));

        // expect
        assertThatThrownBy(() -> this.changesApplicationService.fetchCommitChanges(command))
            .isExactlyInstanceOf(IllegalStateException.class);
    }

    private static Projection.FileProjection sampleFileProjection(FileId fileId)
    {
        FileMetadata metadata = FileMetadata.builder()
            .name("")
            .authors(List.of())
            .organizations(List.of())
            .timestamp(LocalDateTime.now())
            .preprocessorVersion("")
            .originatingSystem("")
            .authorization("")
            .build();
        FileDescription description = FileDescription.of(List.of(), "");
        return Projection.FileProjection.of(
            fileId, "ISO", "IFC4", metadata, description, List.of("1", "2", "3")
        );
    }

    private static Commit sampleCommit(ProjectId projectId, FileId fileId, long commitId, Long parentId)
    {
        List<CommitItem> items = List.of(CommitItem.addItem("value", 0));
        return commitWithContentChanges(projectId, fileId, commitId, parentId, items);
    }
}