package valeriy.knyazhev.architector.application.project.file.conflict;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
public class FileDescriptionConflictException extends Exception
{

    @Nonnull
    private DescriptionConflictChanges changes;

    @Nonnull
    private Long headCommitId;

    public FileDescriptionConflictException(@Nonnull DescriptionConflictChanges changes,
                                            @Nonnull Long headCommitId)
    {
        super("Metadata changes conflict.");
        this.changes = Args.notNull(changes, "Description changes are required.");
        this.headCommitId = Args.notNull(headCommitId, "Head commit identifier is required.");
    }

    @Nonnull
    public DescriptionConflictChanges changes()
    {
        return this.changes;
    }

    public long headCommitId()
    {
        return this.headCommitId;
    }

}
