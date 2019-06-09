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
    private List<ContentConflictBlock> conflictBlocks;

    private ContentConflictChanges(@Nonnull List<ContentConflictBlock> conflictBlocks)
    {
        this.conflictBlocks = Args.notNull(conflictBlocks, "Conflict changes blocks are required.");
    }

    public boolean isEmpty()
    {
        return this.conflictBlocks.isEmpty();
    }

    @Nonnull
    public List<ContentConflictBlock> conflictBlocks()
    {
        return this.conflictBlocks;
    }

    public static ContentConflictChanges of(@Nonnull List<ContentConflictBlock> conflictBlocks)
    {
        return new ContentConflictChanges(conflictBlocks);
    }

    public static ContentConflictChanges empty()
    {
        return new ContentConflictChanges(List.of());
    }

}
