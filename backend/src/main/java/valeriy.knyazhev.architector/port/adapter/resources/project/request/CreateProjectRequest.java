package valeriy.knyazhev.architector.port.adapter.resources.project.request;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class CreateProjectRequest {

    @Nonnull
    private String name;

    @Nonnull
    private String description;

    public CreateProjectRequest(@Nonnull String name,
                                @Nonnull String description) {
        this.name = Args.notBlank(name, "Project name is required.");
        this.description = Args.notNull(description, "Project description is required.");
    }

    @Nonnull
    public String name() {
        return this.name;
    }

    @Nonnull
    public String description() {
        return this.description;
    }

}
