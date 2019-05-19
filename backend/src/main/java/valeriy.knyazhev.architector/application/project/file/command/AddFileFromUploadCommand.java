package valeriy.knyazhev.architector.application.project.file.command;

import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class AddFileFromUploadCommand
{

    @Nonnull
    private String projectId;

    @Nonnull
    private Architector architector;

    @Nonnull
    private MultipartFile content;

    public AddFileFromUploadCommand(@Nonnull String projectId,
                                    @Nonnull Architector architector,
                                    @Nonnull MultipartFile content)
    {
        this.projectId = projectId;
        this.architector = architector;
        this.content = content;
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
    public MultipartFile content()
    {
        return this.content;
    }
}
