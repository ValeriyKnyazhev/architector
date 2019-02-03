package valeriy.knyazhev.architector.application.project.command;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class CreateProjectFromFileCommand {

    @Nonnull
    private String author;

    @Nonnull
    private MultipartFile file;

    public CreateProjectFromFileCommand(@Nonnull String author,
                                        @Nonnull MultipartFile file) {
        this.author = author;
        this.file = file;
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
