package valeriy.knyazhev.architector.domain.model.project.commit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import valeriy.knyazhev.architector.domain.model.util.JsonbType;

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
public class CommitDescription implements Serializable {

    @Nonnull
    // FIXME now the number of files is 1
    private List<CommitFileItem> changedFiles;

    private CommitDescription(@Nonnull List<CommitFileItem> files) {
        this.changedFiles = files;
    }

    @Nonnull
    public static CommitDescription of(@Nonnull List<CommitFileItem> files) {
        return new CommitDescription(files);
    }

    @Nonnull
    public List<CommitFileItem> changedFiles() {
        return this.changedFiles;
    }

    public static class CommitDescriptionJsonbType extends JsonbType<CommitDescription> {

        public CommitDescriptionJsonbType() {
            super(CommitDescription.class);
        }
    }

}
