package valeriy.knyazhev.architector.application.commit.data.changes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.commit.ChangeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class SectionChangesData
{

    @Nonnull
    private List<SectionItem> items;

    public SectionChangesData(@Nonnull List<SectionItem> items)
    {
        this.items = Args.notNull(items, "Section items are required.");
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    public static class SectionItem
    {

        private int oldPosition;

        private int newPosition;

        @Nonnull
        private String value;

        @Nullable
        private ChangeType type;

        public SectionItem(int oldPosition,
                           int newPosition,
                           @Nonnull String value,
                           @Nullable ChangeType type)
        {
            this.oldPosition = oldPosition;
            this.newPosition = newPosition;
            this.value = Args.notNull(value, "Section item value is required.");
            this.type = type;
        }

    }


}
