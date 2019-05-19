package valeriy.knyazhev.architector.application.project.file.command;

import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateFileFromUploadCommand
{

    @Nonnull
    private String projectId;

    @Nonnull
    private String fileId;

    @Nonnull
    private Architector architector;

    @Nonnull
    private MultipartFile content;

    public UpdateFileFromUploadCommand(@Nonnull String projectId,
                                       @Nonnull String fileId,
                                       @Nonnull Architector architector,
                                       @Nonnull MultipartFile content)
    {
        this.projectId = projectId;
        this.fileId = fileId;
        this.architector = architector;
        this.content = content;
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

    @Nonnull
    public MultipartFile content()
    {
        return this.content;
    }
}
