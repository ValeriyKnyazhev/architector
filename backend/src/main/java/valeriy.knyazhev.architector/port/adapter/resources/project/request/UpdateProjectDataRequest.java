package valeriy.knyazhev.architector.port.adapter.resources.project.request;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateProjectDataRequest
{

    @NotNull(message = "Project name is required.")
    @Size(min = 1, max = 50, message = "Project name must have minimum 1 and maximum 50 symbols.")
    private String name;

    @NotNull(message = "Project description is required.")
    private String description;

    public void setName(@Nonnull String name)
    {
        this.name = Args.notBlank(name, "Project name is required.");
    }

    public void setDescription(@Nonnull String description)
    {
        this.description = Args.notNull(description, "Project description is required.");
    }

    @Nonnull
    public String name()
    {
        return this.name;
    }

    @Nonnull
    public String description()
    {
        return this.description;
    }


}
