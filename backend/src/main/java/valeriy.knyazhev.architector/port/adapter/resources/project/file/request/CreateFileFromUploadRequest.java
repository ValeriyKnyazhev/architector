package valeriy.knyazhev.architector.port.adapter.resources.project.file.request;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class CreateFileFromUploadRequest {


    @Nonnull
    private String name;

    public void setName(@Nonnull String name) {
        this.name = Args.notNull(name, "Name is required.");
    }

    @Nonnull
    public String name() {
        return this.name;
    }

}
