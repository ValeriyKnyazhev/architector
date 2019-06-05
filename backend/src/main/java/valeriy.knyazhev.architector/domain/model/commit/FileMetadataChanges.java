package valeriy.knyazhev.architector.domain.model.commit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FileMetadataChanges
{

    private String name;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime timestamp;

    private List<String> authors;

    private List<String> organizations;

    private String preprocessorVersion;

    private String originatingSystem;

    private String authorization;

    @Builder
    private FileMetadataChanges(String name, LocalDateTime timestamp, List<String> authors,
                                List<String> organizations, String preprocessorVersion,
                                String originatingSystem, String authorization)
    {
        this.name = name;
        this.timestamp = timestamp;
        this.authors = authors;
        this.organizations = organizations;
        this.preprocessorVersion = preprocessorVersion;
        this.originatingSystem = originatingSystem;
        this.authorization = authorization;
    }

    protected FileMetadataChanges()
    {
        // empty
    }

    @JsonIgnore
    public boolean isEmpty()
    {
        return this.name == null && this.timestamp == null && this.authors == null &&
               this.organizations == null && this.preprocessorVersion == null &&
               this.originatingSystem == null && this.authorization == null;
    }

    public String name()
    {
        return this.name;
    }

    @Nonnull
    public String newName(@Nonnull String oldValue)
    {
        return this.name != null ? this.name : oldValue;
    }

    public LocalDateTime timestamp()
    {
        return this.timestamp;
    }

    @Nonnull
    public LocalDateTime newTimestamp(@Nonnull LocalDateTime oldValue)
    {
        return this.timestamp != null ? this.timestamp : oldValue;
    }

    public List<String> authors()
    {
        return this.authors;
    }

    @Nonnull
    public List<String> newAuthors(@Nonnull List<String> oldValue)
    {
        return this.authors != null ? this.authors : oldValue;
    }

    public List<String> organizations()
    {
        return this.organizations;
    }

    @Nonnull
    public List<String> newOrganizations(@Nonnull List<String> oldValue)
    {
        return this.organizations != null ? this.organizations : oldValue;
    }

    public String preprocessorVersion()
    {
        return this.preprocessorVersion;
    }

    @Nonnull
    public String newPreprocessorVersion(@Nonnull String oldValue)
    {
        return this.preprocessorVersion != null ? this.preprocessorVersion : oldValue;
    }

    public String originatingSystem()
    {
        return this.originatingSystem;
    }

    @Nonnull
    public String newOriginatingSystem(@Nonnull String oldValue)
    {
        return this.originatingSystem != null ? this.originatingSystem : oldValue;
    }

    public String authorization()
    {
        return this.authorization;
    }

    @Nonnull
    public String newAuthorization(@Nonnull String oldValue)
    {
        return this.authorization != null ? this.authorization : oldValue;
    }

    @Nonnull
    public static FileMetadataChanges empty()
    {
        return new FileMetadataChanges();
    }

}
