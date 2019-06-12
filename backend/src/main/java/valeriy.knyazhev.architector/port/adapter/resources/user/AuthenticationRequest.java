package valeriy.knyazhev.architector.port.adapter.resources.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.util.Args;

import javax.annotation.Nonnull;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Valeriy Knyazhev
 */
public class AuthenticationRequest implements Serializable
{

    @NotNull(message = "Architector email is required.")
    @Email(message = "Architector email is invalid.")
    private String email;

    @NotNull(message = "Architector password is required.")
    @Size(min = 4, message = "Architector password must consist of minimum 4 symbols.")
    private String password;

    public void setEmail(@Nonnull String email)
    {
        this.email = Args.notBlank(email, "Architector email is required.");
    }

    public void setPassword(@Nonnull String password)
    {
        this.password = Args.notNull(password, "Architector password is required.");
    }

    @Nonnull
    public String email()
    {
        return this.email;
    }

    @Nonnull
    public String password()
    {
        return this.password;
    }

}