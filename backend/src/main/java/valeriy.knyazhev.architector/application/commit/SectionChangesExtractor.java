package valeriy.knyazhev.architector.application.commit;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.application.commit.data.changes.SectionChangesData;
import valeriy.knyazhev.architector.domain.model.commit.CommitItem;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class SectionChangesExtractor
{

    private static final int DEFAULT_LINES_OFFSET_SIZE = 3;

    @Nonnull
    private List<String> items;

    @Nonnull
    private List<CommitItem> changes;

    private int linesOffsetSize;

    private SectionChangesExtractor(@Nonnull List<String> items,
                                    @Nonnull List<CommitItem> changes,
                                    int linesOffsetSize)
    {
        this.items = Args.notNull(items, "Items are required.");
        this.changes = Args.notNull(changes, "Changes are required.");
        this.linesOffsetSize = linesOffsetSize;
    }

    @Nonnull
    private List<SectionChangesData> extract()
    {
        // TODO add section extracting
        return Collections.emptyList();
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

