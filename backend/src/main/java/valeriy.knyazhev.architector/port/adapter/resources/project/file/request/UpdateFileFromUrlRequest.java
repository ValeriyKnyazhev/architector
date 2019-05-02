package valeriy.knyazhev.architector.port.adapter.resources.project.file.request;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateFileFromUrlRequest {

    @Nonnull
    private String sourceUrl;

    public void setSourceUrl(@Nonnull String sourceUrl) {
        this.sourceUrl = Args.notBlank(sourceUrl, "Source url is required.");
    }

    @Nonnull
    public String sourceUrl() {
        return this.sourceUrl;
    }

}
