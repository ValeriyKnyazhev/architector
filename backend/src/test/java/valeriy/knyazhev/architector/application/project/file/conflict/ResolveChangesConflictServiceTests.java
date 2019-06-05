package valeriy.knyazhev.architector.application.project.file.conflict;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictChanges;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictChanges.ContentChangesBlock;
import valeriy.knyazhev.architector.domain.model.commit.CommitItem;
import valeriy.knyazhev.architector.domain.model.commit.CommitRepository;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void shouldConflictsIfBlocksAreIntersected()
    {
        // given
        List<String> content = generateContent();
        List<CommitItem> headItems = List.of(
            CommitItem.addItem("new1", 3),
            CommitItem.addItem("new2", 3),
            CommitItem.deleteItem("5", 4)
        );
        List<CommitItem> newItems = List.of(CommitItem.addItem("new3", 3));

        // when
        ContentConflictChanges changes = this.resolveConflictService.checkContentChangesConflicts(
            content, headItems, newItems
        );

        // then
        assertThat(changes.isEmpty()).isFalse();
        List<ContentChangesBlock> headBlocks = changes.headBlocks();
        List<ContentChangesBlock> newBlocks = changes.newBlocks();
    }

    private static List<String> generateContent()
    {
        return IntStream.rangeClosed(1, 10)
            .mapToObj(Integer::toString)
            .collect(Collectors.toList());
    }

}