package valeriy.knyazhev.architector.application.project.file.conflict.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.commit.CommitItem;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Valeriy Knyazhev
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ContentConflictBlock
{

    private final int startIndex;

    private final int endIndex;

    @Nonnull
    private final List<ContentChangesBlock> headBlocks;

    @Nonnull
    private final List<ContentChangesBlock> newBlocks;

    public ContentConflictBlock(int startIndex,
                                int endIndex,
                                @Nonnull List<ContentChangesBlock> headBlocks,
                                @Nonnull List<ContentChangesBlock> newBlocks)
    {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.headBlocks = Args.notNull(headBlocks, "Head changes blocks are required.");
        this.newBlocks = Args.notNull(newBlocks, "New changes blocks are required.");
    }

    public int startIndex()
    {
        return this.startIndex;
    }

    public int endIndex()
    {
        return this.endIndex;
    }

    @Nonnull
    public List<ContentChangesBlock> headBlocks()
    {
        return this.headBlocks;
    }

    @Nonnull
    public List<ContentChangesBlock> newBlocks()
    {
        return this.newBlocks;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class ContentChangesBlock
    {

        private final int startIndex;

        private final int endIndex;

        private final List<CommitItem> items;

        public ContentChangesBlock(int startIndex, int endIndex, @Nonnull List<CommitItem> items)
        {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.items = new ArrayList<>(items);
        }

        public int startIndex()
        {
            return this.startIndex;
        }

        public int endIndex()
        {
            return this.endIndex;
        }

        @Nonnull
        public List<CommitItem> items()
        {
            return this.items;
        }

    }

}
