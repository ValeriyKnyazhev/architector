package valeriy.knyazhev.architector.application.project.file.conflict.exception;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictBlock;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Valeriy Knyazhev
 */
public class FileContentConflictException extends Exception
{

    @Nonnull
    private List<String> oldContent;

    @Nonnull
    private List<ContentConflictBlock> conflictBlocks;

    @Nonnull
    private Long headCommitId;

    public FileContentConflictException(@Nonnull List<String> oldContent,
                                        @Nonnull List<ContentConflictBlock> conflictBlocks,
                                        @Nonnull Long headCommitId)
    {
        super("File content changes conflict.");
        this.oldContent = Args.notNull(oldContent, "File content is required.");
        this.conflictBlocks = Args.notNull(conflictBlocks, "Conflict changes blocks are required.");
        this.headCommitId = Args.notNull(headCommitId, "Head commit identifier is required.");
    }

    @Nonnull
    public List<String> oldContent()
    {
        return this.oldContent;
    }

    @Nonnull
    public List<ContentConflictBlock> conflictBlocks()
    {
        return this.conflictBlocks;
    }

    public long headCommitId()
    {
        return this.headCommitId;
    }

}
