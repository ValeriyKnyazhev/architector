package valeriy.knyazhev.architector.application.project.command;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class CreateProjectCommand
{

    @Nonnull
    private String name;

    @Nonnull
    private String author;

    @Nonnull
    private String description;

    public CreateProjectCommand(@Nonnull String name,
                                @Nonnull String author,
                                @Nonnull String description)
    {
        this.name = name;
        this.author = author;
        this.description = description;
    }

    @Nonnull
    public String name()
    {
        return this.name;
    }

    @Nonnull
    public String author()
    {
        return this.author;
    }

    @Nonnull
    public String description()
    {
        return this.description;
    }

}
