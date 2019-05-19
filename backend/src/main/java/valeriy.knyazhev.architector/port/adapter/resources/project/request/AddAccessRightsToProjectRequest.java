package valeriy.knyazhev.architector.port.adapter.resources.project.request;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class AddAccessRightsToProjectRequest
{

    @NotNull(message = "Architector email is required.")
    @Size(min = 1, max = 255, message = "Architector email must have minimum 1 and maximum 255 symbols.")
    private String email;

    @NotNull(message = "Access rights is required.")
    private AccessRights accessRights;

    public void setEmail(@Nonnull String email)
    {
        this.email = Args.notBlank(email, "Architector email is required.");
    }

    public void setAccessRights(@Nonnull AccessRights accessRights)
    {
        this.accessRights = Args.notNull(accessRights, "Access rights is required.");
    }

    @Nonnull
    public String email()
    {
        return this.email;
    }

    @Nonnull
    public AccessRights accessRights()
    {
        return this.accessRights;
    }

    public static enum AccessRights
    {
        WRITE, READ
    }


}
