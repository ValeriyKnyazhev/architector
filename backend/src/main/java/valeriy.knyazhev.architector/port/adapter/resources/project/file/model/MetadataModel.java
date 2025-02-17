package valeriy.knyazhev.architector.port.adapter.resources.project.file.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;
import valeriy.knyazhev.architector.domain.model.util.serialization.ArchitectorLocalDateTimeSerializer;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class MetadataModel
{

    @Nonnull
    private String name;

    @Nonnull
    @JsonSerialize(using = ArchitectorLocalDateTimeSerializer.class)
    private LocalDateTime timestamp;

    @Nonnull
    private List<String> authors;

    @Nonnull
    private List<String> organizations;

    @Nonnull
    private String preprocessorVersion;

    @Nonnull
    private String originatingSystem;

    @Nonnull
    private String authorization;

    private MetadataModel(@Nonnull String name,
                          @Nonnull LocalDateTime timestamp,
                          @Nonnull List<String> authors,
                          @Nonnull List<String> organizations,
                          @Nonnull String preprocessorVersion,
                          @Nonnull String originatingSystem,
                          @Nonnull String authorization)
    {
        this.name = name;
        this.timestamp = timestamp;
        this.authors = authors;
        this.organizations = organizations;
        this.preprocessorVersion = preprocessorVersion;
        this.originatingSystem = originatingSystem;
        this.authorization = authorization;
    }

    @Nonnull
    public static MetadataModel of(@Nonnull FileMetadata metadata)
    {
        return new MetadataModel(
            metadata.name(), metadata.timestamp(), metadata.authors(),
            metadata.organizations(), metadata.preprocessorVersion(),
            metadata.originatingSystem(), metadata.authorization()
        );
    }

}
