package valeriy.knyazhev.architector.domain.model.project.commit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static valeriy.knyazhev.architector.domain.model.project.commit.ChangeType.ADDITION;
import static valeriy.knyazhev.architector.domain.model.project.commit.ChangeType.DELETION;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@EqualsAndHashCode
@JsonAutoDetect(fieldVisibility = ANY)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommitItem {

    @Nonnull
    private String value;

    @Nonnull
    private ChangeType type;

    private int position;

    private CommitItem(@Nonnull String value, @Nonnull ChangeType type, int position) {
        this.value = value;
        this.type = type;
        this.position = position;
    }

    @Nonnull
    public static CommitItem addItem(@Nonnull String value, int position) {
        return new CommitItem(value, ADDITION, position);
    }

    @Nonnull
    public static CommitItem deleteItem(@Nonnull String value, int position) {
        return new CommitItem(value, DELETION, position);
    }

    @Nonnull
    public String value() {
        return this.value;
    }

    @Nonnull
    public ChangeType type() {
        return this.type;
    }

    public int position() {
        return this.position;
    }
}
