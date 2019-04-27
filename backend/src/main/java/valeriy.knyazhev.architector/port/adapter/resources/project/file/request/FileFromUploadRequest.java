package valeriy.knyazhev.architector.port.adapter.resources.project.file.request;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class FileFromUploadRequest {


    @Nonnull
    private String message;

    public void setMessage(@Nonnull String message) {
        this.message = Args.notNull(message, "Message is required.");
    }

    @Nonnull
    public String message() {
        return this.message;
    }

}
