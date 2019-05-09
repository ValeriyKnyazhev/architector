package valeriy.knyazhev.architector.port.adapter.resources.project.file.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.annotation.Nonnull;
import java.util.List;

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
    private MetadataModel metadata;

    @Nonnull
    private DescriptionModel description;

    @Nonnull
    private String content;

    public FileContentModel(@Nonnull String fileId,
                            @Nonnull MetadataModel metadata,
                            @Nonnull DescriptionModel description,
                            @Nonnull String content)
    {
        this.fileId = fileId;
        this.metadata = metadata;
        this.description = description;
        this.content = content;
    }

    public FileContentModel(@Nonnull String fileId,
                            @Nonnull MetadataModel metadata,
                            @Nonnull DescriptionModel description,
                            @Nonnull List<String> items)
    {
        this.fileId = fileId;
        this.metadata = metadata;
        this.description = description;
        // FIXME for webclient
        this.content = String.join(System.lineSeparator() + System.lineSeparator(), items);
    }

}