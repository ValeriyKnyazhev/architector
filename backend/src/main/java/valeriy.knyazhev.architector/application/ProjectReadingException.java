package valeriy.knyazhev.architector.application;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
public class ProjectReadingException extends IllegalStateException {

    public ProjectReadingException(@Nonnull String projectPath) {
        super("Unable to read project " + Args.notNull(projectPath, "Project path is required.") + ".");
    }

    public ProjectReadingException() {
        super("Unable to read project.");
    }

}
