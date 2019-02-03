package valeriy.knyazhev.architector.port.adapter.project;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class CreateProjectFromUrlRequest {

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
