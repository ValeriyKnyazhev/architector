package valeriy.knyazhev.architector.application.project.command;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateProjectDataCommand
{

    @Nonnull
    private String projectId;

    @Nonnull
    private String name;

    @Nonnull
    private String description;

    @Nonnull
    private String author;

    public UpdateProjectDataCommand(@Nonnull String projectId,
                                    @Nonnull String name,
                                    @Nonnull String description,
                                    @Nonnull String author)
    {
        this.projectId = Args.notBlank(projectId, "Project identifier is required.");
        this.name = Args.notBlank(name, "Project name is required.");
        this.description = Args.notNull(description, "Project description is required.");
        this.author = Args.notBlank(author, "Author is required.");
    }

    @Nonnull
    public ProjectId projectId()
    {
        return ProjectId.of(this.projectId);
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

    @Nonnull
    public String author()
    {
        return this.author;
    }

}
