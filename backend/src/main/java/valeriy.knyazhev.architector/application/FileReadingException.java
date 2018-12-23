package valeriy.knyazhev.architector.application;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
public class FileReadingException extends IllegalStateException {

    public FileReadingException(@Nonnull String projectPath) {
        super("Unable to read project " + Args.notNull(projectPath, "Project path is required.") + ".");
    }

    public FileReadingException() {
        super("Unable to read project.");
    }

}
