package valeriy.knyazhev.architector.domain.model.project.commit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@EqualsAndHashCode
@JsonAutoDetect(fieldVisibility = ANY)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommitFileItem {

    @Nonnull
    private FileId fileId;

    @Nonnull
    private List<CommitItem> items;

    private CommitFileItem(@Nonnull FileId fileId,
                           @Nonnull List<CommitItem> items) {
        this.fileId = fileId;
        this.items = items.stream()
                .sorted(CommitItem::compareTo)
                .collect(Collectors.toList());
    }

    @Nonnull
    public static CommitFileItem of(@Nonnull FileId fileId,
                                    @Nonnull List<CommitItem> items) {
        return new CommitFileItem(fileId, items);
    }

    @Nonnull
    public FileId fileId() {
        return this.fileId;
    }

    @Nonnull
    public List<CommitItem> items() {
        return this.items;
    }

}
