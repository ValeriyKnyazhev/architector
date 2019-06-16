package valeriy.knyazhev.architector.application.project.file.validation;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * @author Valeriy Knyazhev
 */
public class InvalidFileContentException extends IllegalStateException
{

    @Nonnull
    private List<ChangedEntity> entities;

    public InvalidFileContentException(@Nonnull List<ChangedEntity> entities)
    {
        super("Invalid references found.");
        this.entities = Args.notNull(entities, "Entities are required.");
    }

    @Nonnull
    public List<ChangedEntity> entities()
    {
        return this.entities;
    }

}
