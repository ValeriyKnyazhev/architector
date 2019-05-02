package valeriy.knyazhev.architector.domain.model.project.file;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileDescription {

    @Nonnull
    private List<String> descriptions;

    @Nonnull
    private String implementationLevel;

    private FileDescription(@Nonnull List<String> descriptions, @Nonnull String implementationLevel) {
        this.descriptions = descriptions;
        this.implementationLevel = implementationLevel;
    }

    @Nonnull
    public static FileDescription of(@Nonnull List<String> descriptions, @Nonnull String implementationLevel) {
        return new FileDescription(descriptions, implementationLevel);
    }

    @Nonnull
    public List<String> descriptions() {
        return this.descriptions;
    }

    @Nonnull
    public String implementationLevel() {
        return this.implementationLevel;
    }

}
