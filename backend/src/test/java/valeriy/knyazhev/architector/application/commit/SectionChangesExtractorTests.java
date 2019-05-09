package valeriy.knyazhev.architector.application.commit;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.application.commit.data.changes.SectionChangesData;
import valeriy.knyazhev.architector.application.commit.data.changes.SectionChangesData.SectionItem;
import valeriy.knyazhev.architector.domain.model.commit.CommitItem;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static valeriy.knyazhev.architector.domain.model.commit.CommitItem.addItem;
import static valeriy.knyazhev.architector.domain.model.commit.CommitItem.deleteItem;

/**
 * @author Valeriy Knyazhev
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SectionChangesExtractor.class)
public class SectionChangesExtractorTests
{

    @Test
    public void shouldReturnEmptySectionsList()
    {
        // when
        List<SectionChangesData> sections = SectionChangesExtractor.sectionsOf(sampleContent())
            .applyChanges(emptyList())
            .extract();

        // then
        assertThat(sections).as("Content should not have changes.").isEmpty();
    }

    @Test
    public void shouldReturnSectionWithAdditionWithoutContentLines()
    {
        // given
        CommitItem change = addItem("new", 4);

        // when
        List<SectionChangesData> sections = SectionChangesExtractor.sectionsOf(sampleContent())
            .applyChanges(singletonList(change))
            .withLinesOffset(0)
            .extract();

        // then
        assertThat(sections.size()).as("1 section should be extracted.").isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        List<SectionItem> sectionItems = sections.get(0).items();
        softly.assertThat(sectionItems.size()).isEqualTo(1);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                sectionItems,
                singletonList(SectionItem.addedItem(change.position(), change.value()))
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithDeletionWithoutContentLines()
    {
        // given
        List<String> content = sampleContent();
        int position = 4;
        CommitItem change = deleteItem(content.get(position), position);

        // when
        List<SectionChangesData> sections = SectionChangesExtractor.sectionsOf(content)
            .applyChanges(singletonList(change))
            .withLinesOffset(0)
            .extract();

        // then
        assertThat(sections.size()).as("1 section should be extracted.").isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        List<SectionItem> sectionItems = sections.get(0).items();
        softly.assertThat(sectionItems.size()).isEqualTo(1);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                sectionItems,
                singletonList(SectionItem.deletedItem(change.position(), change.value()))
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithAddition()
    {
        // given
        List<String> content = sampleContent();
        int position = 4;
        CommitItem change = addItem("new", position);
        int linesOffsetSize = 1;

        // when
        List<SectionChangesData> sections = SectionChangesExtractor.sectionsOf(content)
            .applyChanges(singletonList(change))
            .withLinesOffset(linesOffsetSize)
            .extract();

        // then
        assertThat(sections.size()).as("1 section should be extracted.").isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        List<SectionItem> sectionItems = sections.get(0).items();
        softly.assertThat(sectionItems.size()).isEqualTo(1 + 2 * linesOffsetSize);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                sectionItems,
                asList(
                    SectionItem.item(position - 1, position - 1, content.get(position - 1)),
                    SectionItem.addedItem(position, change.value()),
                    SectionItem.item(position, position + 1, content.get(position))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithDeletion()
    {
        // given
        List<String> content = sampleContent();
        int position = 4;
        CommitItem change = deleteItem(content.get(position), position);
        int linesOffsetSize = 1;

        // when
        List<SectionChangesData> sections = SectionChangesExtractor.sectionsOf(content)
            .applyChanges(singletonList(change))
            .withLinesOffset(linesOffsetSize)
            .extract();

        // then
        assertThat(sections.size()).as("1 section should be extracted.").isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        List<SectionItem> sectionItems = sections.get(0).items();
        softly.assertThat(sectionItems.size()).isEqualTo(1 + 2 * linesOffsetSize);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                sectionItems,
                asList(
                    SectionItem.item(position - 1, position - 1, content.get(position - 1)),
                    SectionItem.deletedItem(position, change.value()),
                    SectionItem.item(position + 1, position, content.get(position + 1))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithAdditionInStart()
    {
        // given
        List<String> content = sampleContent();
        int position = 0;
        CommitItem change = addItem("new", position);
        int linesOffsetSize = 1;

        // when
        List<SectionChangesData> sections = SectionChangesExtractor.sectionsOf(content)
            .applyChanges(singletonList(change))
            .withLinesOffset(linesOffsetSize)
            .extract();

        // then
        assertThat(sections.size()).as("1 section should be extracted.").isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        List<SectionItem> sectionItems = sections.get(0).items();
        softly.assertThat(sectionItems.size()).isEqualTo(1 + linesOffsetSize);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                sectionItems,
                asList(
                    SectionItem.addedItem(position, change.value()),
                    SectionItem.item(position, position + 1, content.get(position))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithDeletionInStart()
    {
        // given
        List<String> content = sampleContent();
        int position = 0;
        CommitItem change = deleteItem(content.get(position), position);
        int linesOffsetSize = 1;

        // when
        List<SectionChangesData> sections = SectionChangesExtractor.sectionsOf(content)
            .applyChanges(singletonList(change))
            .withLinesOffset(linesOffsetSize)
            .extract();

        // then
        assertThat(sections.size()).as("1 section should be extracted.").isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        List<SectionItem> sectionItems = sections.get(0).items();
        softly.assertThat(sectionItems.size()).isEqualTo(1 + linesOffsetSize);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                sectionItems,
                asList(
                    SectionItem.deletedItem(position, change.value()),
                    SectionItem.item(position + 1, position, content.get(position + 1))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithFewAdditions()
    {
        // given
        List<String> content = sampleContent();
        int position = 4;
        List<CommitItem> changes = asList(
            addItem("new 1", position),
            addItem("new 2", position)
        );
        int linesOffsetSize = 1;

        // when
        List<SectionChangesData> sections = SectionChangesExtractor.sectionsOf(content)
            .applyChanges(changes)
            .withLinesOffset(linesOffsetSize)
            .extract();

        // then
        assertThat(sections.size()).as("1 section should be extracted.").isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        List<SectionItem> sectionItems = sections.get(0).items();
        softly.assertThat(sectionItems.size()).isEqualTo(changes.size() + 2 * linesOffsetSize);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                sectionItems,
                asList(
                    SectionItem.item(position - 1, position - 1, content.get(position - 1)),
                    SectionItem.addedItem(position, "new 1"),
                    SectionItem.addedItem(position + 1, "new 2"),
                    SectionItem.item(position, position + 2, content.get(position))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithFewDeletions()
    {
        // given
        List<String> content = sampleContent();
        int position = 4;
        List<CommitItem> changes = asList(
            deleteItem(content.get(position), position),
            deleteItem(content.get(position + 1), position + 1)
        );
        int linesOffsetSize = 1;

        // when
        List<SectionChangesData> sections = SectionChangesExtractor.sectionsOf(content)
            .applyChanges(changes)
            .withLinesOffset(linesOffsetSize)
            .extract();

        // then
        assertThat(sections.size()).as("1 section should be extracted.").isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        List<SectionItem> sectionItems = sections.get(0).items();
        softly.assertThat(sectionItems.size()).isEqualTo(changes.size() + 2 * linesOffsetSize);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                sectionItems,
                asList(
                    SectionItem.item(position - 1, position - 1, content.get(position - 1)),
                    SectionItem.deletedItem(position, content.get(position)),
                    SectionItem.deletedItem(position + 1, content.get(position + 1)),
                    SectionItem.item(position + 2, position, content.get(position + 2))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    private static List<String> sampleContent()
    {
        return IntStream.range(0, 10)
            .mapToObj(Integer::toString)
            .collect(Collectors.toList());
    }

}