package valeriy.knyazhev.architector.application.project.file;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.application.project.file.ChangesConflictApplicationService.MetadataConflictChanges;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
public class FileMetadataConflictException extends Exception
{

    @Nonnull
    private MetadataConflictChanges changes;

    public FileMetadataConflictException(@Nonnull MetadataConflictChanges changes)
    {
        super("Metadata changes conflict.");
        this.changes = Args.notNull(changes, "Metadata changes are required.");
    }

    @Nonnull
    public MetadataConflictChanges changes()
    {
        return this.changes;
    }

}
