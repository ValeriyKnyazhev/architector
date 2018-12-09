package valeriy.knyazhev.architector.domain.model.project.file;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileDescription {

    @Nonnull
    private String description;

    @Nonnull
    private String implementationLevel;

    private FileDescription(@Nonnull String description, @Nonnull String implementationLevel) {
        this.description = description;
        this.implementationLevel = implementationLevel;
    }

    @Nonnull
    public static FileDescription of(@Nonnull String description, @Nonnull String implementationLevel) {
        return new FileDescription(description, implementationLevel);
    }

    @Nonnull
    public String description() {
        return this.description;
    }

    @Nonnull
    public String implementationLevel() {
        return this.implementationLevel;
    }

}
