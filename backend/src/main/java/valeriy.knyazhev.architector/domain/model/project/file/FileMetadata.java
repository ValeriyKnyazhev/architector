package valeriy.knyazhev.architector.domain.model.project.file;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileMetadata {

    @Nonnull
    private String name;

    @Nonnull
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
    private String authorisation;

    @Builder
    private FileMetadata(@Nonnull String name, @Nonnull LocalDate timestamp, @Nonnull List<String> authors,
                         @Nonnull List<String> organizations, @Nonnull String preprocessorVersion,
                         @Nonnull String originatingSystem, @Nonnull String authorisation) {
        this.name = name;
        this.timestamp = timestamp;
        this.authors = authors;
        this.organizations = organizations;
        this.preprocessorVersion = preprocessorVersion;
        this.originatingSystem = originatingSystem;
        this.authorisation = authorisation;
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
    public String authorisation() {
        return this.authorisation;
    }

}
