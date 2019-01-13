package valeriy.knyazhev.architector.port.adapter.project;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
public class AddFileFromUrlCommand {

    @Nonnull
    private String fileUrl;

    public void setFileUrl(@Nonnull String fileUrl) {
        this.fileUrl = Args.notBlank(fileUrl, "File url is required.");
    }

    public String fileUrl() {
        return this.fileUrl;
    }
}
