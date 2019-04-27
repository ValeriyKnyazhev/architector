package valeriy.knyazhev.architector.application.project.file.command;

import valeriy.knyazhev.architector.domain.model.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class AddFileFromUrlCommand {

    @Nonnull
    private String projectId;

    @Nonnull
    private String author;

    @Nonnull
    private String name;

    @Nonnull
    private String sourceUrl;

    public AddFileFromUrlCommand(@Nonnull String projectId,
                                 @Nonnull String author,
                                 @Nonnull String name,
                                 @Nonnull String sourceUrl) {
        this.projectId = projectId;
        this.author = author;
        this.name = name;
        this.sourceUrl = sourceUrl;
    }

    @Nonnull
    public ProjectId projectId() {
        return ProjectId.of(this.projectId);
    }

    @Nonnull
    public String author() {
        return this.author;
    }

    @Nonnull
    public String name() {
        return this.name;
    }

    @Nonnull
    public String sourceUrl() {
        return this.sourceUrl;
    }
}
