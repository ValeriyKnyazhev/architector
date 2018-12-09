package valeriy.knyazhev.architector.domain.model.project.file;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

import static valeriy.knyazhev.architector.domain.model.util.ListValuesUtils.extractValues;
import static valeriy.knyazhev.architector.domain.model.util.ListValuesUtils.mapValue;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileDescription {

    @Nonnull
    private String descriptions;

    @Nonnull
    private String implementationLevel;

    private FileDescription(@Nonnull String descriptions, @Nonnull String implementationLevel) {
        this.descriptions = descriptions;
        this.implementationLevel = implementationLevel;
    }

    @Nonnull
    public static FileDescription of(@Nonnull Collection<String> descriptions, @Nonnull String implementationLevel) {
        return new FileDescription(mapValue(descriptions), implementationLevel);
    }

    @Nonnull
    public List<String> descriptions() {
        return extractValues(this.descriptions);
    }

    @Nonnull
    public String implementationLevel() {
        return this.implementationLevel;
    }

}
