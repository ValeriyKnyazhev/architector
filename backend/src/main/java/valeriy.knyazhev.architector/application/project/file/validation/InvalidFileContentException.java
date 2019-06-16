package valeriy.knyazhev.architector.application.project.file.validation;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author Valeriy Knyazhev
 */
public class InvalidFileContentException extends IllegalStateException
{

    @Nonnull
    private Set<Integer> entities;

    public InvalidFileContentException(@Nonnull Set<Integer> entities)
    {
        super("Invalid references found.");
        this.entities = Args.notNull(entities, "Entities are required.");
    }

    @Nonnull
    public Set<Integer> entities()
    {
        return this.entities;
    }

}
