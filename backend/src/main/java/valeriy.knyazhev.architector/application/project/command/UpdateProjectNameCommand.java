package valeriy.knyazhev.architector.application.project.command;

import valeriy.knyazhev.architector.domain.model.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateProjectNameCommand {

    @Nonnull
    private String projectId;

    @Nonnull
    private String name;

    @Nonnull
    private String author;

    public UpdateProjectNameCommand(@Nonnull String projectId,
                                    @Nonnull String name,
                                    @Nonnull String author) {
        this.projectId = projectId;
        this.name = name;
        this.author = author;
    }

    @Nonnull
    public ProjectId projectId()
    {
        return ProjectId.of(this.projectId);
    }

    @Nonnull
    public String name() {
        return this.name;
    }

    @Nonnull
    public String author() {
        return this.author;
    }

}
