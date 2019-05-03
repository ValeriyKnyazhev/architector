package valeriy.knyazhev.architector.port.adapter.resources.project.request;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateProjectDescriptionRequest
{

    @NotNull(message = "Project description is required.")
    private String description;

    public UpdateProjectDescriptionRequest(@Nonnull String description)
    {
        this.description = Args.notNull(description, "Project description is required.");
    }

    @Nonnull
    public String description()
    {
        return this.description;
    }


}
