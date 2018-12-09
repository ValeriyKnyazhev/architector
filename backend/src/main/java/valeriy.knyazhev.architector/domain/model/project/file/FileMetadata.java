package valeriy.knyazhev.architector.domain.model.project.file;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.time.LocalDate;

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
    private String author;

    @Nonnull
    private String organization;

    @Nonnull
    private String preprocessorVersion;

    @Nonnull
    private String originatingSystem;

    @Nonnull
    private String authorisation;

    @Builder
    private FileMetadata(@Nonnull String name, @Nonnull LocalDate timestamp, @Nonnull String author,
                         @Nonnull String organization, @Nonnull String preprocessorVersion,
                         @Nonnull String originatingSystem, @Nonnull String authorisation) {
        this.name = name;
        this.timestamp = timestamp;
        this.author = author;
        this.organization = organization;
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
    public String author() {
        return this.author;
    }

    @Nonnull
    public String organization() {
        return this.organization;
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
