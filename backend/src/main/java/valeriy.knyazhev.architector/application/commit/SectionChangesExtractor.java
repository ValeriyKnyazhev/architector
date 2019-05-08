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
        boolean isNewAddition = true;
        int lastPosition = this.changes.get(0).position();
        List<SectionItem> newSectionItems = new LinkedList<>();
        // FIXME calculate result with lines offset
        for (CommitItem change : changes)
        {
            if (isNewAddition)
            {
                // TODO add offset lines from the begin of section
            }

            //TODO add adding items
            isNewAddition = false;


            if (change.position() - this.linesOffsetSize > lastPosition + this.linesOffsetSize + 1)
            {
                // Get offset lines from the end of section
                AtomicInteger lineIndex = new AtomicInteger(lastPosition + 1);
                List<String> lines = fetchLines(lineIndex.get(), lineIndex.get() + this.linesOffsetSize);
                newSectionItems.addAll(
                    lines.stream()
                        .map(line ->
                        SectionItem.item(
                            lineIndex.get(), lineIndex.getAndIncrement() + curOffset, line
                        )
                    )
                        .collect(Collectors.toList())
                );

                // Create and add new section
                sections.add(new SectionChangesData(newSectionItems));
                newSectionItems = new LinkedList<>();
                isNewAddition = true;
            }
        }
        return sections;
    }

    @Nonnull
    private List<String> fetchLines(int fromIndex, int toIndex)
    {
        Args.check(fromIndex <= toIndex, "FromIndex must not be greater toIndex.");
        Args.check(toIndex >= 0 && fromIndex < this.itemsSize,
            "Something went wrong: check SectionChangesExtractor.");
        return this.items.subList(
            fromIndex < 0 ? 0 : fromIndex,
            toIndex > this.itemsSize ? this.itemsSize : toIndex + 1
        );
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

