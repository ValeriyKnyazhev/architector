package valeriy.knyazhev.architector.application.user;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
public class ArchitectorNotFoundException extends IllegalStateException
{

    public ArchitectorNotFoundException(@Nonnull String email)
    {
        super("Unable to find architector by email " + Args.notNull(email, "Email is required.") + ".");
    }

}
