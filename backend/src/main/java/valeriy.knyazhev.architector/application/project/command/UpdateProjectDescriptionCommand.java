package valeriy.knyazhev.architector.application.project.command;

import valeriy.knyazhev.architector.domain.model.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateProjectDescriptionCommand
{

    @Nonnull
    private String projectId;

    @Nonnull
    private String description;

    @Nonnull
    private String author;

    public UpdateProjectDescriptionCommand(@Nonnull String projectId,
                                           @Nonnull String description,
                                           @Nonnull String author)
    {
        this.projectId = projectId;
        this.description = description;
        this.author = author;
    }

    @Nonnull
    public ProjectId projectId()
    {
        return ProjectId.of(this.projectId);
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
