package valeriy.knyazhev.architector.domain.model.project.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@EqualsAndHashCode
@JsonAutoDetect(fieldVisibility = ANY)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileContent implements Serializable {

    @Nonnull
    private List<String> items;

    private FileContent(@Nonnull List<String> items) {
        this.items = items;
    }

    @Nonnull
    public static FileContent of(@Nonnull List<String> items) {
        return new FileContent(items);
    }

    @Nonnull
    public List<String> items() {
        return this.items;
    }

}
