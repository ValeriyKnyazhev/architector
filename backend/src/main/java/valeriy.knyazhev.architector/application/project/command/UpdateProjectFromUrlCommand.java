package valeriy.knyazhev.architector.application.project.command;

import valeriy.knyazhev.architector.domain.model.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateProjectFromUrlCommand {

    @Nonnull
    private String projectId;

    @Nonnull
    private String author;

    @Nonnull
    private String sourceUrl;

    public UpdateProjectFromUrlCommand(@Nonnull String projectId,
                                       @Nonnull String author,
                                       @Nonnull String sourceUrl) {
        this.projectId = projectId;
        this.author = author;
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
    public String sourceUrl() {
        return this.sourceUrl;
    }
}
