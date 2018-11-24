package valeriy.knyazhev.architector.domain.model.project;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectDescription {

    @Nonnull
    private String description;

    @Nonnull
    private String implementationLevel;

    private ProjectDescription(@Nonnull String description, @Nonnull String implementationLevel) {
        this.description = description;
        this.implementationLevel = implementationLevel;
    }

    @Nonnull
    public static ProjectDescription of(@Nonnull String description, @Nonnull String implementationLevel) {
        return new ProjectDescription(description, implementationLevel);
    }

}
