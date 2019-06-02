package valeriy.knyazhev.architector.application.project.file;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.application.project.file.ChangesConflictApplicationService.DescriptionConflictChanges;
import valeriy.knyazhev.architector.application.project.file.ChangesConflictApplicationService.MetadataConflictChanges;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
public class FileDescriptionConflictException extends Exception
{

    @Nonnull
    private DescriptionConflictChanges changes;

    public FileDescriptionConflictException(@Nonnull DescriptionConflictChanges changes)
    {
        super("Metadata changes conflict.");
        this.changes = Args.notNull(changes, "Description changes are required.");
    }

    @Nonnull
    public DescriptionConflictChanges changes()
    {
        return this.changes;
    }

}
