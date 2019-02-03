package valeriy.knyazhev.architector.domain.model.project.commit;

import javax.annotation.Nonnull;

import static valeriy.knyazhev.architector.domain.model.project.commit.ChangeType.ADDITION;
import static valeriy.knyazhev.architector.domain.model.project.commit.ChangeType.DELETION;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
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

    public CommitItem addItem(@Nonnull String value, int position) {
        return new CommitItem(value, ADDITION, position);
    }

    public CommitItem deleteItem(@Nonnull String value, int position) {
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
