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
public class ContentConflictChanges
{

    @Nonnull
    private List<ContentChangesBlock> headBlocks;

    @Nonnull
    private List<ContentChangesBlock> newBlocks;


    private ContentConflictChanges(@Nonnull List<ContentChangesBlock> headBlocks,
                                   @Nonnull List<ContentChangesBlock> newBlocks)
    {
        this.headBlocks = Args.notNull(headBlocks, "Head changes blocks are required.");
        this.newBlocks = Args.notNull(newBlocks, "New changes blocks are required.");
    }

    public boolean isEmpty()
    {
        return this.headBlocks.isEmpty() || this.newBlocks.isEmpty();
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

    public static ContentConflictChanges of(@Nonnull List<ContentChangesBlock> headBlocks,
                                            @Nonnull List<ContentChangesBlock> newBlocks)
    {
        return new ContentConflictChanges(headBlocks, newBlocks);
    }

    public static ContentConflictChanges empty()
    {
        return new ContentConflictChanges(List.of(), List.of());
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
