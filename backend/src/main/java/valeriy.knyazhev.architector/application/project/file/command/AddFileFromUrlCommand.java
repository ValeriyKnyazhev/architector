package valeriy.knyazhev.architector.application.project.file.command;

import org.apache.commons.lang3.arch.Processor;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class AddFileFromUrlCommand
{

    @Nonnull
    private String projectId;

    @Nonnull
    private Architector architector;

    @Nonnull
    private String sourceUrl;

    public AddFileFromUrlCommand(@Nonnull String projectId,
                                 @Nonnull Architector architector,
                                 @Nonnull String sourceUrl)
    {
        this.projectId = projectId;
        this.architector = architector;
        this.sourceUrl = sourceUrl;
    }

    @Nonnull
    public ProjectId projectId()
    {
        return ProjectId.of(this.projectId);
    }

    @Nonnull
    public Architector architector()
    {
        return this.architector;
    }

    @Nonnull
    public String sourceUrl()
    {
        return this.sourceUrl;
    }
}
