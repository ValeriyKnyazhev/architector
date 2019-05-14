package valeriy.knyazhev.architector.application.user;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
public class ArchitectorAlreadyExistException extends IllegalStateException
{

    public ArchitectorAlreadyExistException(@Nonnull String email)
    {
        super("Architector with email " + Args.notNull(email, "Email is required.") + " already exists.");
    }

}
