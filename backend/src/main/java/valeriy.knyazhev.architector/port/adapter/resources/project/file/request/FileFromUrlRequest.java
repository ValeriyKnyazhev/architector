package valeriy.knyazhev.architector.port.adapter.resources.project.file.request;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class FileFromUrlRequest {

    @Nonnull
    private String sourceUrl;

    @Nonnull
    private String message;

    public void setSourceUrl(@Nonnull String sourceUrl) {
        this.sourceUrl = Args.notBlank(sourceUrl, "Source url is required.");
    }

    public void setMessage(@Nonnull String message) {
        this.message = Args.notNull(message, "Message is required.");
    }

    @Nonnull
    public String sourceUrl() {
        return this.sourceUrl;
    }

    @Nonnull
    public String message() {
        return this.message;
    }

}
