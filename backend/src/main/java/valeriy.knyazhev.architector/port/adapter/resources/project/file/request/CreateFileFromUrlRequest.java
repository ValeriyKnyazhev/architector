package valeriy.knyazhev.architector.port.adapter.resources.project.file.request;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class CreateFileFromUrlRequest {

    @Nonnull
    private String sourceUrl;

    @Nonnull
    private String name;

    public void setSourceUrl(@Nonnull String sourceUrl) {
        this.sourceUrl = Args.notBlank(sourceUrl, "Source url is required.");
    }

    public void setName(@Nonnull String name) {
        this.name = Args.notNull(name, "Name is required.");
    }

    @Nonnull
    public String sourceUrl() {
        return this.sourceUrl;
    }

    @Nonnull
    public String name() {
        return this.name;
    }

}
