package valeriy.knyazhev.architector.application.project.command;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class CreateProjectFromUrlCommand {

    @Nonnull
    private String author;

    @Nonnull
    private String sourceUrl;

    public CreateProjectFromUrlCommand(@Nonnull String author,
                                       @Nonnull String sourceUrl) {
        this.author = author;
        this.sourceUrl = sourceUrl;
    }

    @Nonnull
    public String author() {
        return this.author;
    }

    @Nonnull
    public String sourceUrl() {
        return this.sourceUrl;
    }
}
