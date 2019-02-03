package valeriy.knyazhev.architector.application.project.command;

import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateProjectFromFileCommand {

    @Nonnull
    private String projectId;

    @Nonnull
    private String author;

    @Nonnull
    private MultipartFile file;

    public UpdateProjectFromFileCommand(@Nonnull String projectId,
                                        @Nonnull String author,
                                        @Nonnull MultipartFile file) {
        this.projectId = projectId;
        this.author = author;
        this.file = file;
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
    public MultipartFile file() {
        return this.file;
    }
}
