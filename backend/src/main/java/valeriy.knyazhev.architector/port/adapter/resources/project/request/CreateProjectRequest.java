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
@Data
public class CreateProjectRequest
{

    @NotNull(message = "Project name is required.")
    @Size(min = 1, max = 50, message = "Project name must have minimum 1 and maximum 50 symbols.")
    private String name;

    @NotNull(message = "Project name is required.")
    private String description;

}
