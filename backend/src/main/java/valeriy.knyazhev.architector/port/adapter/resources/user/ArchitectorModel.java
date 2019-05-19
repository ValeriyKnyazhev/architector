package valeriy.knyazhev.architector.port.adapter.resources.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ArchitectorModel
{

    @Nonnull
    private String email;

    public ArchitectorModel(@Nonnull String email)
    {
        this.email = Args.notNull(email, "Architector email is required.");
    }


}
