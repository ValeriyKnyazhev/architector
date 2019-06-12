package valeriy.knyazhev.architector.application.project.file.conflict;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.application.project.file.conflict.command.ResolveContentConflictCommand;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictBlock;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictBlock.ContentChangesBlock;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictChanges;
import valeriy.knyazhev.architector.domain.model.commit.CommitItem;
import valeriy.knyazhev.architector.domain.model.commit.CommitRepository;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.factory.ProjectObjectFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * @author Valeriy Knyazhev
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ResolveChangesConflictService.class)
public class ResolveChangesConflictServiceTests
{

    @Autowired
    private ResolveChangesConflictService resolveConflictService;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private CommitRepository commitRepository;

    @Test
    public void shouldNoConflictsIfOnlyHeadItemsExist()
    {
        // given
        List<String> content = generateContent();
        List<CommitItem> headItems = List.of();
        List<CommitItem> newItems = List.of(CommitItem.addItem("new", 1));

        // when
        ContentConflictChanges changes = this.resolveConflictService.checkContentChangesConflicts(
            content, headItems, newItems
        );

        // then
        assertThat(changes.isEmpty()).isTrue();
    }

    @Test
    public void shouldNoConflictsIfOnlyNewItemsExist()
    {
        // given
        List<String> content = generateContent();
        List<CommitItem> headItems = List.of(CommitItem.addItem("new", 1));
        List<CommitItem> newItems = List.of();

        // when
        ContentConflictChanges changes = this.resolveConflictService.checkContentChangesConflicts(
            content, headItems, newItems
        );

        // then
        assertThat(changes.isEmpty()).isTrue();
    }

    @Test
    public void shouldNoConflictsIfBlocksAreNotIntersected()
    {
        // given
        List<String> content = generateContent();
        List<CommitItem> headItems = List.of(CommitItem.addItem("new", 1));
        List<CommitItem> newItems = List.of(CommitItem.addItem("new", 4));

        // when
        ContentConflictChanges changes = this.resolveConflictService.checkContentChangesConflicts(
            content, headItems, newItems
        );

        // then
        assertThat(changes.isEmpty()).isTrue();
    }

    @Test
    public void shouldConflictsIfDeletedBlocksAreIntersected()
    {
        // given
        List<String> content = generateContent();
        List<CommitItem> headItems = List.of(
            CommitItem.deleteItem("4", 3),
            CommitItem.deleteItem("5", 4),
            CommitItem.deleteItem("6", 5)
        );
        List<CommitItem> newItems = List.of(
            CommitItem.deleteItem("4", 3),
            CommitItem.deleteItem("6", 5)
        );

        // when
        ContentConflictChanges changes = this.resolveConflictService.checkContentChangesConflicts(
            content, headItems, newItems
        );

        // then
        assertThat(changes.isEmpty()).isFalse();
        List<ContentConflictBlock> conflictBlocks = changes.conflictBlocks();
        assertThat(conflictBlocks).size().isEqualTo(1);
        ContentConflictBlock conflictBlock = conflictBlocks.get(0);
        List<ContentChangesBlock> headBlocks = conflictBlock.headBlocks();
        List<ContentChangesBlock> newBlocks = conflictBlock.newBlocks();
        assertThat(headBlocks).size().isEqualTo(1);
        assertThat(newBlocks).size().isEqualTo(2);
    }

    @Test
    public void shouldResolveFileContentConflict()
    {
        // given
        Project project = ProjectObjectFactory.projectWithFiles("author");
        ProjectId projectId = project.projectId();
        project.updateCurrentCommitId(3L);
        FileId fileId = project.files().get(0).fileId();
        long headCommitId = 2L;
        String content = "new";
        Architector architector = new Architector();
        architector.setEmail("author");
        ResolveContentConflictCommand command = new ResolveContentConflictCommand(
            projectId.id(), fileId.id(), architector, headCommitId, content
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // when
        boolean resolved = this.resolveConflictService.resolveContentChangesConflict(command);

        // then
        assertThat(resolved).isTrue();
    }

    @Test
    public void shouldNotResolveFileContentConflictIfProjectNotFound()
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        long headCommitId = 2L;
        String content = "new";
        Architector architector = new Architector();
        architector.setEmail("author");
        ResolveContentConflictCommand command = new ResolveContentConflictCommand(
            projectId.id(), fileId.id(), architector, headCommitId, content
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> this.resolveConflictService.resolveContentChangesConflict(command))
            .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    public void shouldNotResolveFileContentConflictIfFileNotFound()
    {
        // given
        Project project = ProjectObjectFactory.emptyProject("author");
        ProjectId projectId = project.projectId();
        project.updateCurrentCommitId(3L);
        FileId fileId = FileId.nextId();
        long headCommitId = 2L;
        String content = "new";
        Architector architector = new Architector();
        architector.setEmail("author");
        ResolveContentConflictCommand command = new ResolveContentConflictCommand(
            projectId.id(), fileId.id(), architector, headCommitId, content
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() -> this.resolveConflictService.resolveContentChangesConflict(command))
            .isExactlyInstanceOf(FileNotFoundException.class);
    }

    @Test
    public void shouldNotResolveFileContentConflictIfProjectCommitIdNotExist()
    {
        // given
        Project project = ProjectObjectFactory.projectWithFiles("author");
        ProjectId projectId = project.projectId();
        FileId fileId = project.files().get(0).fileId();
        long headCommitId = 2L;
        String content = "new";
        Architector architector = new Architector();
        architector.setEmail("author");
        ResolveContentConflictCommand command = new ResolveContentConflictCommand(
            projectId.id(), fileId.id(), architector, headCommitId, content
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() -> this.resolveConflictService.resolveContentChangesConflict(command))
        .isExactlyInstanceOf(IllegalStateException.class);
    }

    private static List<String> generateContent()
    {
        return IntStream.rangeClosed(1, 10)
            .mapToObj(Integer::toString)
            .collect(Collectors.toList());
    }

}