package valeriy.knyazhev.architector.domain.model.project.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FileDescription
{

    @Nonnull
    private List<String> descriptions;

    @Nonnull
    private String implementationLevel;

    private FileDescription(@Nonnull List<String> descriptions, @Nonnull String implementationLevel)
    {
        this.descriptions = checkAndMapList(descriptions);
        this.implementationLevel = implementationLevel;
    }

    protected FileDescription()
    {
        // empty
    }

    @Nonnull
    public List<String> descriptions()
    {
        return this.descriptions;
    }

    @Nonnull
    public String implementationLevel()
    {
        return this.implementationLevel;
    }

    @Nonnull
    // TODO remove later
    private static List<String> checkAndMapList(@Nonnull List<String> items)
    {
        return items.stream().anyMatch(item -> !item.isEmpty())
               ? items
               : emptyList();
    }

    @Nonnull
    public static FileDescription of(@Nonnull List<String> descriptions, @Nonnull String implementationLevel)
    {
        return new FileDescription(descriptions, implementationLevel);
    }

}
