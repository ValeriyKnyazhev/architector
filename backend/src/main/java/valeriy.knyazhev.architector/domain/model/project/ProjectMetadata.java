package valeriy.knyazhev.architector.domain.model.project;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMetadata {

    @Nonnull
    private String name;

    @Nonnull
    private String timestamp;

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

    @Builder(builderMethodName = "constructor", buildMethodName = "construct")
    private ProjectMetadata(@Nonnull String name, @Nonnull String timestamp, @Nonnull String author,
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

}
