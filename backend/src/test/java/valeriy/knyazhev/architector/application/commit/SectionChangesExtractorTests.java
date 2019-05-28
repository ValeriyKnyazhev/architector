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
                singletonList(SectionItem.addedItem(change.position() + 1, change.value()))
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
        CommitItem change = deleteItem(content.get(position - 1), position);

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
                    SectionItem.item(position, position, content.get(position - 1)),
                    SectionItem.addedItem(position + 1, change.value()),
                    SectionItem.item(position + 1, position + 2, content.get(position))
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
        CommitItem change = deleteItem(content.get(position - 1), position);
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
                    SectionItem.item(position - 1, position - 1, content.get(position - 2)),
                    SectionItem.deletedItem(position, change.value()),
                    SectionItem.item(position + 1, position, content.get(position))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithAdditionInTheBeginning()
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
                    SectionItem.addedItem(position + 1, change.value()),
                    SectionItem.item(position + 1, position + 2, content.get(position))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithDeletionInTheBeginning()
    {
        // given
        List<String> content = sampleContent();
        int position = 1;
        CommitItem change = deleteItem(content.get(position - 1), position);
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
                    SectionItem.item(position + 1, position, content.get(position))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithAdditionAtTheEnd()
    {
        // given
        List<String> content = sampleContent();
        int position = content.size();
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
                    SectionItem.item(position, position, content.get(position - 1)),
                    SectionItem.addedItem(position + 1, change.value())
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithDeletionAtTheEnd()
    {
        // given
        List<String> content = sampleContent();
        int position = content.size();
        CommitItem change = deleteItem(content.get(position - 1), position);
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
                    SectionItem.item(position - 1, position - 1, content.get(position - 2)),
                    SectionItem.deletedItem(position, change.value())
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
                    SectionItem.item(position, position, content.get(position - 1)),
                    SectionItem.addedItem(position + 1, "new 1"),
                    SectionItem.addedItem(position + 2, "new 2"),
                    SectionItem.item(position + 1, position + 3, content.get(position))
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
            deleteItem(content.get(position - 1), position),
            deleteItem(content.get(position), position + 1)
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
                    SectionItem.item(position - 1, position - 1, content.get(position - 2)),
                    SectionItem.deletedItem(position, content.get(position - 1)),
                    SectionItem.deletedItem(position + 1, content.get(position)),
                    SectionItem.item(position + 2, position, content.get(position + 1))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithAdditionsAtDistanceEqualsTwoLinesOffsetSize()
    {
        // given
        List<String> content = sampleContent();
        int position = 2;
        int linesOffsetSize = 1;
        List<CommitItem> changes = asList(
            addItem("new 1", position),
            addItem("new 2", position + 2 * linesOffsetSize)
        );

        // when
        List<SectionChangesData> sections = SectionChangesExtractor.sectionsOf(content)
            .applyChanges(changes)
            .withLinesOffset(linesOffsetSize)
            .extract();

        // then
        assertThat(sections.size()).as("1 section should be extracted.").isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        List<SectionItem> sectionItems = sections.get(0).items();
        softly.assertThat(sectionItems.size()).isEqualTo(changes.size() + 4 * linesOffsetSize);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                sectionItems,
                asList(
                    SectionItem.item(position, position, content.get(position - 1)),
                    SectionItem.addedItem(position + 1, "new 1"),
                    SectionItem.item(position + 1, position + 2, content.get(position)),
                    SectionItem.item(position + 2, position + 3, content.get(position + 1)),
                    SectionItem.addedItem(position + 4, "new 2"),
                    SectionItem.item(position + 3, position + 5, content.get(position + 2))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithDeletionsAtDistanceEqualsTwoLinesOffsetSize()
    {
        // given
        List<String> content = sampleContent();
        int linesOffsetSize = 1;
        int firstPosition = 2;
        int secondPosition = firstPosition + 1 + 2 * linesOffsetSize;
        List<CommitItem> changes = asList(
            deleteItem(content.get(firstPosition - 1), firstPosition),
            deleteItem(content.get(secondPosition - 1), secondPosition)
        );

        // when
        List<SectionChangesData> sections = SectionChangesExtractor.sectionsOf(content)
            .applyChanges(changes)
            .withLinesOffset(linesOffsetSize)
            .extract();

        // then
        assertThat(sections.size()).as("1 section should be extracted.").isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        List<SectionItem> sectionItems = sections.get(0).items();
        softly.assertThat(sectionItems.size()).isEqualTo(changes.size() + 4 * linesOffsetSize);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                sectionItems,
                asList(
                    SectionItem.item(firstPosition - 1, firstPosition - 1, content.get(firstPosition - 2)),
                    SectionItem.deletedItem(firstPosition, content.get(firstPosition - 1)),
                    SectionItem.item(firstPosition + 1, firstPosition, content.get(firstPosition)),
                    SectionItem.item(secondPosition - 1, secondPosition - 2, content.get(secondPosition - 2)),
                    SectionItem.deletedItem(secondPosition, content.get(secondPosition - 1)),
                    SectionItem.item(secondPosition + 1, secondPosition - 1, content.get(secondPosition))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnTwoSectionsWithAdditions()
    {
        // given
        List<String> content = sampleContent();
        int linesOffsetSize = 1;
        int firstPosition = 2;
        int secondPosition = firstPosition + 1 + 2 * linesOffsetSize;
        List<CommitItem> changes = asList(
            addItem("new 1", firstPosition),
            addItem("new 2", secondPosition)
        );

        // when
        List<SectionChangesData> sections = SectionChangesExtractor.sectionsOf(content)
            .applyChanges(changes)
            .withLinesOffset(linesOffsetSize)
            .extract();

        // then
        assertThat(sections.size()).as("2 sections should be extracted.").isEqualTo(2);
        SoftAssertions softly = new SoftAssertions();
        List<SectionItem> firstSectionItems = sections.get(0).items();
        List<SectionItem> secondSectionItems = sections.get(1).items();
        softly.assertThat(firstSectionItems.size()).isEqualTo(1 + 2 * linesOffsetSize);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                firstSectionItems,
                asList(
                    SectionItem.item(firstPosition, firstPosition, content.get(firstPosition - 1)),
                    SectionItem.addedItem(firstPosition + 1, "new 1"),
                    SectionItem.item(firstPosition + 1, firstPosition + 2, content.get(firstPosition))
                )
            )
        ).isTrue();
        softly.assertThat(secondSectionItems.size()).isEqualTo(1 + 2 * linesOffsetSize);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                secondSectionItems,
                asList(
                    SectionItem.item(secondPosition, secondPosition + 1, content.get(secondPosition - 1)),
                    SectionItem.addedItem(secondPosition + 2, "new 2"),
                    SectionItem.item(secondPosition + 1, secondPosition + 3, content.get(secondPosition))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnTwoSectionsWithDeletions()
    {
        // given
        List<String> content = sampleContent();
        int linesOffsetSize = 1;
        int firstPosition = 2;
        int secondPosition = firstPosition + 2 + 2 * linesOffsetSize;
        List<CommitItem> changes = asList(
            deleteItem(content.get(firstPosition - 1), firstPosition),
            deleteItem(content.get(secondPosition - 1), secondPosition)
        );

        // when
        List<SectionChangesData> sections = SectionChangesExtractor.sectionsOf(content)
            .applyChanges(changes)
            .withLinesOffset(linesOffsetSize)
            .extract();

        // then
        assertThat(sections.size()).as("2 sections should be extracted.").isEqualTo(2);
        SoftAssertions softly = new SoftAssertions();
        List<SectionItem> firstSectionItems = sections.get(0).items();
        List<SectionItem> secondSectionItems = sections.get(1).items();
        softly.assertThat(firstSectionItems.size()).isEqualTo(1 + 2 * linesOffsetSize);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                firstSectionItems,
                asList(
                    SectionItem.item(firstPosition - 1, firstPosition - 1, content.get(firstPosition - 2)),
                    SectionItem.deletedItem(firstPosition, content.get(firstPosition - 1)),
                    SectionItem.item(firstPosition + 1, firstPosition, content.get(firstPosition))
                )
            )
        ).isTrue();
        softly.assertThat(secondSectionItems.size()).isEqualTo(1 + 2 * linesOffsetSize);
        softly.assertThat(
            CollectionUtils.isEqualCollection(
                secondSectionItems,
                asList(
                    SectionItem.item(secondPosition - 1, secondPosition - 2, content.get(secondPosition - 2)),
                    SectionItem.deletedItem(secondPosition, content.get(secondPosition - 1)),
                    SectionItem.item(secondPosition + 1, secondPosition - 1, content.get(secondPosition))
                )
            )
        ).isTrue();
        softly.assertAll();
    }

    @Test
    public void shouldReturnSectionWithChangedLine()
    {
        // given
        List<String> content = sampleContent();
        int linesOffsetSize = 1;
        int position = 4;
        List<CommitItem> changes = asList(
            addItem("new", position - 1),
            deleteItem(content.get(position - 1), position)
        );

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
                    SectionItem.item(position - 1, position - 1, content.get(position - 2)),
                    SectionItem.addedItem(position, "new"),
                    SectionItem.deletedItem(position, content.get(position - 1)),
                    SectionItem.item(position + 1, position + 1, content.get(position))
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