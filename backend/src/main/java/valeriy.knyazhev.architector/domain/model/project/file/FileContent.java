package valeriy.knyazhev.architector.domain.model.project.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@EqualsAndHashCode
@JsonAutoDetect(fieldVisibility = ANY)
public class FileContent
{

    @Nonnull
    private List<String> items;

    private FileContent(@Nonnull List<String> items)
    {
        this.items = items;
    }

    protected FileContent()
    {
        // empty
    }

    @Nonnull
    public List<String> items()
    {
        return this.items;
    }

    @Nonnull
    public static FileContent of(@Nonnull List<String> items)
    {
        return new FileContent(items);
    }

}
