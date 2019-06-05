package valeriy.knyazhev.architector.application.project.file.conflict.exception;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictChanges.ContentChangesBlock;

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
    private List<ContentChangesBlock> headBlocks;

    @Nonnull
    private List<ContentChangesBlock> newBlocks;

    @Nonnull
    private Long headCommitId;

    public FileContentConflictException(@Nonnull List<String> oldContent,
                                        @Nonnull List<ContentChangesBlock> headBlocks,
                                        @Nonnull List<ContentChangesBlock> newBlocks,
                                        @Nonnull Long headCommitId)
    {
        super("File content changes conflict.");
        this.oldContent = Args.notNull(oldContent, "File content is required.");
        this.headBlocks = Args.notNull(headBlocks, "Head changes blocks are required.");
        this.newBlocks = Args.notNull(newBlocks, "New changes blocks are required.");
        this.headCommitId = Args.notNull(headCommitId, "Head commit identifier is required.");
    }

    @Nonnull
    public List<String> oldContent()
    {
        return this.oldContent;
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


    public long headCommitId()
    {
        return this.headCommitId;
    }

}
