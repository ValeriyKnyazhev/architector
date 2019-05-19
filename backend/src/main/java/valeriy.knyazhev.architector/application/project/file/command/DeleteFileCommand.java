package valeriy.knyazhev.architector.application.project.file.command;

import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class DeleteFileCommand
{

    @Nonnull
    private String projectId;

    @Nonnull
    private String fileId;

    @Nonnull
    private Architector architector;

    public DeleteFileCommand(@Nonnull String projectId,
                             @Nonnull String fileId,
                             @Nonnull Architector architector)
    {
        this.projectId = projectId;
        this.fileId = fileId;
        this.architector = architector;
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
    public Architector architector()
    {
        return this.architector;
    }

}
