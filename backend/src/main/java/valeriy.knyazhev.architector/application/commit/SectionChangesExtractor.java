package valeriy.knyazhev.architector.application.commit;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.application.commit.data.changes.SectionChangesData;
import valeriy.knyazhev.architector.application.commit.data.changes.SectionChangesData.SectionItem;
import valeriy.knyazhev.architector.domain.model.commit.CommitItem;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static valeriy.knyazhev.architector.domain.model.commit.ChangeType.ADDITION;
import static valeriy.knyazhev.architector.domain.model.commit.ChangeType.DELETION;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class SectionChangesExtractor
{

    private static final int DEFAULT_LINES_OFFSET_SIZE = 3;

    private final int itemsSize;

    private final int linesOffsetSize;

    @Nonnull
    private List<String> items;
    @Nonnull
    private List<CommitItem> changes;

    private SectionChangesExtractor(@Nonnull List<String> items,
                                    @Nonnull List<CommitItem> changes,
                                    int linesOffsetSize)
    {
        this.items = Args.notNull(items, "Items are required.");
        this.itemsSize = items.size();
        this.changes = Args.notNull(changes, "Changes are required.");
        this.linesOffsetSize = linesOffsetSize;
    }

    @Nonnull
    private List<SectionChangesData> extract()
    {
        if (changes.isEmpty())
        {
            return emptyList();
        }
        List<SectionChangesData> sections = new LinkedList<>();
        int curOffset = 0;
        boolean isNewChange = true;
        int lastPosition = this.changes.get(0).position();
        List<SectionItem> newSectionItems = new LinkedList<>();
        for (CommitItem change : changes)
        {
            if (isNewSection(change, lastPosition))
            {
                // Add offset lines from the content to the end of section
                int startPosition = lastPosition + 1;
                newSectionItems.addAll(extractLastContentLines(startPosition, curOffset));

                // Create and add new section
                sections.add(new SectionChangesData(newSectionItems));
                lastPosition = change.position();
                isNewChange = true;
                newSectionItems = new LinkedList<>();
            }
            if (isNewChange && !isBeginOfContent(change))
            {
                // Add offset lines from the content to the start of section
                int endPosition = change.type() == ADDITION
                    ? change.position() + 1
                    : change.position();
                newSectionItems.addAll(extractFirstContentLines(endPosition, curOffset));
            } else
            {
                if (change.position() > lastPosition + 1)
                {
                    // Add offset lines from the content to the section space between changes
                    int endPosition = change.type() == ADDITION
                        ? change.position()
                        : change.position() - 1;
                    newSectionItems.addAll(
                        extractSectionItemsFromContent(lastPosition + 1, endPosition, curOffset)
                    );
                }
            }

            // Add changed line to section
            isNewChange = false;
            SectionItem newItem = null;
            if (change.type() == ADDITION)
            {
                curOffset++;
                newItem = SectionItem.addedItem(change.position() + curOffset, change.value());
            } else
            {
                newItem = SectionItem.deletedItem(change.position(), change.value());
                curOffset--;
            }
            lastPosition = change.position();
            newSectionItems.add(newItem);
        }
        if (!isEndOfContent(lastPosition))
        {
            int startPosition = lastPosition + 1;
            newSectionItems.addAll(extractLastContentLines(startPosition, curOffset));
        }
        sections.add(new SectionChangesData(newSectionItems));
        return sections;
    }

    private boolean isNewSection(@Nonnull CommitItem change, int position)
    {
        return (
                   change.type() == ADDITION &&
                   change.position() - this.linesOffsetSize > position + this.linesOffsetSize
               ) ||
               (
                   change.type() == DELETION &&
                   change.position() - this.linesOffsetSize > position + this.linesOffsetSize + 1
               );
    }

    private boolean isBeginOfContent(@Nonnull CommitItem change)
    {
        return (change.type() == ADDITION && change.position() == 0) ||
               (change.type() == DELETION && change.position() == 1);
    }

    private boolean isEndOfContent(int position)
    {
        return position == this.itemsSize;
    }

    @Nonnull
    private List<SectionItem> extractFirstContentLines(int position, int curOffset)
    {
        return extractSectionItemsFromContent(
            position - this.linesOffsetSize, position - 1, curOffset
        );
    }

    @Nonnull
    private List<SectionItem> extractLastContentLines(int position, int curOffset)
    {
        return extractSectionItemsFromContent(
            position, position - 1 + this.linesOffsetSize, curOffset
        );
    }

    @Nonnull
    private List<SectionItem> extractSectionItemsFromContent(int startIndex, int endIndex, int curOffset)
    {
        startIndex = startIndex < 1 ? 0 : startIndex - 1;
        endIndex = endIndex > this.itemsSize ? this.itemsSize - 1 : endIndex - 1;
        AtomicInteger lineIndex = new AtomicInteger(startIndex + 1);
        return fetchLines(startIndex, endIndex)
            .stream()
            .map(line ->
                SectionItem.item(
                    lineIndex.get(), lineIndex.getAndIncrement() + curOffset, line
                )
            )
            .collect(Collectors.toList());
    }

    @Nonnull
    private List<String> fetchLines(int fromIndex, int toIndex)
    {
        if (fromIndex > toIndex)
        {
            return emptyList();
        }
        Args.check(fromIndex >= 0 && toIndex < this.itemsSize,
            "Something went wrong: check SectionChangesExtractor.");
        return this.items.subList(fromIndex, toIndex + 1);
    }

    public static ExtractorContentBuilder sectionsOf(@Nonnull List<String> items)
    {
        return new ExtractorContentBuilder(items);
    }

    public static class ExtractorContentBuilder
    {

        @Nonnull
        private final List<String> items;

        private ExtractorContentBuilder(@Nonnull List<String> items)
        {
            this.items = Args.notNull(items, "Items are required.");
        }

        public ExtractorContentWithChangesBuilder applyChanges(@Nonnull List<CommitItem> changes)
        {
            return new ExtractorContentWithChangesBuilder(this.items, changes);
        }

    }

    public static class ExtractorContentWithChangesBuilder
    {

        @Nonnull
        private final List<String> items;

        @Nonnull
        private final List<CommitItem> changes;

        private int linesOffsetSize = DEFAULT_LINES_OFFSET_SIZE;

        private ExtractorContentWithChangesBuilder(@Nonnull List<String> items,
                                                   @Nonnull List<CommitItem> changes)
        {
            this.items = Args.notNull(items, "Items are required.");
            this.changes = Args.notNull(changes, "Changes are required.");
        }

        public ExtractorContentWithChangesBuilder withLinesOffset(int linesOffsetSize)
        {
            Args.check(linesOffsetSize >= 0, "Lines offset size must not be negative.");
            this.linesOffsetSize = linesOffsetSize;
            return this;
        }

        @Nonnull
        public List<SectionChangesData> extract()
        {
            SectionChangesExtractor extractor = new SectionChangesExtractor(
                this.items, this.changes, this.linesOffsetSize
            );
            return extractor.extract();
        }

    }

}

