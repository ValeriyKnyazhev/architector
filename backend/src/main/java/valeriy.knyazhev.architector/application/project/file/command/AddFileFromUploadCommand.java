package valeriy.knyazhev.architector.application.project.file.command;

import org.springframework.web.multipart.MultipartFile;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class AddFileFromUploadCommand {

    @Nonnull
    private String projectId;

    @Nonnull
    private String author;

    @Nonnull
    private String name;

    @Nonnull
    private MultipartFile content;

    public AddFileFromUploadCommand(@Nonnull String projectId,
                                    @Nonnull String author,
                                    @Nonnull String name,
                                    @Nonnull MultipartFile content) {
        this.projectId = projectId;
        this.author = author;
        this.name = name;
        this.content = content;
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
        return this.name();
    }

    @Nonnull
    public MultipartFile content() {
        return this.content;
    }
}
