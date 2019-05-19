package valeriy.knyazhev.architector.application.project.file.command;

import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateFileFromUrlCommand
{

    @Nonnull
    private String projectId;

    @Nonnull
    private String fileId;

    @Nonnull
    private Architector architector;

    @Nonnull
    private String sourceUrl;

    public UpdateFileFromUrlCommand(@Nonnull String projectId,
                                    @Nonnull String fileId,
                                    @Nonnull Architector architector,
                                    @Nonnull String sourceUrl)
    {
        this.projectId = projectId;
        this.fileId = fileId;
        this.architector = architector;
        this.sourceUrl = sourceUrl;
    }

    @Nonnull
    public ProjectId projectId()
    {
        return ProjectId.of(this.projectId);
    }

    @Nonnull
    public FileId fileId()
    {
        return FileId.of(this.fileId);
    }

    @Nonnull
    public Architector author()
    {
        return this.architector;
    }

    @Nonnull
    public String sourceUrl()
    {
        return this.sourceUrl;
    }
}
