package valeriy.knyazhev.architector.domain.model.project.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FileMetadata
{

    @Nonnull
    private String name;

    @Nonnull
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
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

    @Builder
    private FileMetadata(@Nonnull String name, @Nonnull LocalDateTime timestamp, @Nonnull List<String> authors,
                         @Nonnull List<String> organizations, @Nonnull String preprocessorVersion,
                         @Nonnull String originatingSystem, @Nonnull String authorization)
    {
        this.name = name;
        this.timestamp = timestamp;
        this.authors = checkAndMapList(authors);
        this.organizations = checkAndMapList(organizations);
        this.preprocessorVersion = preprocessorVersion;
        this.originatingSystem = originatingSystem;
        this.authorization = authorization;
    }

    protected FileMetadata()
    {
        // empty
    }

    @Nonnull
    public String name()
    {
        return this.name;
    }

    @Nonnull
    public LocalDateTime timestamp()
    {
        return this.timestamp;
    }

    @Nonnull
    public List<String> authors()
    {
        return this.authors;
    }

    @Nonnull
    public List<String> organizations()
    {
        return this.organizations;
    }

    @Nonnull
    public String preprocessorVersion()
    {
        return this.preprocessorVersion;
    }

    @Nonnull
    public String originatingSystem()
    {
        return this.originatingSystem;
    }

    @Nonnull
    public String authorization()
    {
        return this.authorization;
    }

    @Nonnull
    // TODO remove later
    private static List<String> checkAndMapList(@Nonnull List<String> items)
    {
        return items.stream().anyMatch(item -> !item.isEmpty())
               ? items
               : emptyList();
    }

}
