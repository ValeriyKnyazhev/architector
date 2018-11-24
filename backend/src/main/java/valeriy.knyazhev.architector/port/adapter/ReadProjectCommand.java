package valeriy.knyazhev.architector.port.adapter;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
public class ReadProjectCommand {

    @Nonnull
    private String projectUrl;

    public void setProjectUrl(@Nonnull String projectUrl) {
        this.projectUrl = Args.notBlank(projectUrl, "Project url is required.");
    }

    public String projectUrl() {
        return this.projectUrl;
    }
}
