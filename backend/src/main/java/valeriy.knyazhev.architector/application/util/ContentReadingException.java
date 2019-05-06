package valeriy.knyazhev.architector.application.util;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
public class ContentReadingException extends RuntimeException
{

    public ContentReadingException(@Nonnull String sourcePath)
    {
        super("Unable to read source " + Args.notNull(sourcePath, "Source path is required.") + ".");
    }

    public ContentReadingException()
    {
        super("Unable to read source data.");
    }

}
