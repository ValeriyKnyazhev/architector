package valeriy.knyazhev.architector.application.project.file.conflict;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
public class FileMetadataConflictException extends Exception
{

    @Nonnull
    private MetadataConflictChanges changes;

    @Nonnull
    private Long headCommitId;

    public FileMetadataConflictException(@Nonnull MetadataConflictChanges changes,
                                         @Nonnull Long headCommitId)
    {
        super("Metadata changes conflict.");
        this.changes = Args.notNull(changes, "Metadata changes are required.");
        this.headCommitId = Args.notNull(headCommitId, "Head commit identifier is required.");
    }

    @Nonnull
    public MetadataConflictChanges changes()
    {
        return this.changes;
    }

    public long headCommitId()
    {
        return this.headCommitId;
    }

}
