package valeriy.knyazhev.architector.domain.model.project.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Builder;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FileMetadata {

    @Nonnull
    private String name;

    @Nonnull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate timestamp;

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
    private FileMetadata(@Nonnull String name, @Nonnull LocalDate timestamp, @Nonnull List<String> authors,
                         @Nonnull List<String> organizations, @Nonnull String preprocessorVersion,
                         @Nonnull String originatingSystem, @Nonnull String authorization) {
        this.name = name;
        this.timestamp = timestamp;
        this.authors = authors;
        this.organizations = organizations;
        this.preprocessorVersion = preprocessorVersion;
        this.originatingSystem = originatingSystem;
        this.authorization = authorization;
    }

    protected FileMetadata() {
        // empty
    }

    @Nonnull
    public String name() {
        return this.name;
    }

    @Nonnull
    public LocalDate timestamp() {
        return this.timestamp;
    }

    @Nonnull
    public List<String> authors() {
        return this.authors;
    }

    @Nonnull
    public List<String> organizations() {
        return this.organizations;
    }

    @Nonnull
    public String preprocessorVersion() {
        return this.preprocessorVersion;
    }

    @Nonnull
    public String originatingSystem() {
        return this.originatingSystem;
    }

    @Nonnull
    public String authorization() {
        return this.authorization;
    }

}
