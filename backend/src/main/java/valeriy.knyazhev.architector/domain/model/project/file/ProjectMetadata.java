package valeriy.knyazhev.architector.domain.model.project.file;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static valeriy.knyazhev.architector.domain.model.util.ListValuesUtils.extractValues;
import static valeriy.knyazhev.architector.domain.model.util.ListValuesUtils.mapValue;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMetadata {

    @Nonnull
    private String name;

    @Nonnull
    private LocalDate timestamp;

    @Nonnull
    private String authors;

    @Nonnull
    private String organizations;

    @Nonnull
    private String preprocessorVersion;

    @Nonnull
    private String originatingSystem;

    @Nonnull
    private String authorisation;

    @Builder
    private ProjectMetadata(@Nonnull String name, @Nonnull LocalDate timestamp, @Nonnull Collection<String> authors,
                            @Nonnull Collection<String> organizations, @Nonnull String preprocessorVersion,
                            @Nonnull String originatingSystem, @Nonnull String authorisation) {
        this.name = name;
        this.timestamp = timestamp;
        this.authors = mapValue(authors);
        this.organizations = mapValue(organizations);
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
        return extractValues(this.authors);
    }

    @Nonnull
    public List<String> organizations() {
        return extractValues(this.organizations);
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
