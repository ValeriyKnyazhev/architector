package valeriy.knyazhev.architector.application.project.file.command;

import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

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
    private String author;

    @Nonnull
    private MultipartFile content;

    public UpdateFileFromUploadCommand(@Nonnull String projectId,
                                       @Nonnull String fileId,
                                       @Nonnull String author,
                                       @Nonnull MultipartFile content)
    {
        this.projectId = projectId;
        this.fileId = fileId;
        this.author = author;
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
    public String author()
    {
        return this.author;
    }

    @Nonnull
    public MultipartFile content()
    {
        return this.content;
    }
}
