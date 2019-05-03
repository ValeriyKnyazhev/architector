package valeriy.knyazhev.architector.port.adapter.resources.project.file.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class FileContentModel
{

    @Nonnull
    private String fileId;

    @Nonnull
    private String name;

    @Nonnull
    private String content;

    public FileContentModel(@Nonnull String fileId, @Nonnull String name, @Nonnull String content)
    {
        this.fileId = fileId;
        this.name = name;
        this.content = content;
    }

    public FileContentModel(@Nonnull String fileId, @Nonnull String name, @Nonnull List<String> items)
    {
        this.fileId = fileId;
        this.name = name;
        this.content = items.stream()
            .map(FileContentModel::wrap)
            .collect(Collectors.joining(""));
    }

    @Nonnull
    private static String wrap(@Nonnull String item)
    {
        return "<div className=\"content-item__data\">" + item + "</div>";
    }

}