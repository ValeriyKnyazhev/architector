package valeriy.knyazhev.architector.port.adapter.resources.project.request;

import lombok.Data;
import org.apache.http.util.Args;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class CreateProjectRequest
{

    @NotNull(message = "Project name is required.")
    @Size(min = 1, max = 50, message = "Project name must have minimum 1 and maximum 50 symbols.")
    private String name;

    @NotNull(message = "Project name is required.")
    private String description;

    public void setName(@Nonnull String name)
    {
        this.name = name;
    }

    public void setDescription(@Nonnull String description)
    {
        this.description = description;
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
