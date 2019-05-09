package valeriy.knyazhev.architector.application.commit.data.changes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.EqualsAndHashCode;
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

    @Nonnull
    public List<SectionItem> items()
    {
        return this.items;
    }

    @EqualsAndHashCode
    @JsonAutoDetect(fieldVisibility = ANY)
    public static class SectionItem
    {

        @Nullable
        private Integer oldPosition;

        @Nullable
        private Integer newPosition;

        @Nonnull
        private String value;

        @Nullable
        private ChangeType type;

        private SectionItem(@Nullable Integer oldPosition,
                            @Nullable Integer newPosition,
                            @Nonnull String value,
                            @Nullable ChangeType type)
        {
            this.oldPosition = oldPosition;
            this.newPosition = newPosition;
            this.value = Args.notNull(value, "Section item value is required.");
            this.type = type;
        }

        @Nullable
        private Integer oldPosition()
        {
            return this.oldPosition;
        }

        @Nullable
        private Integer newPosition()
        {
            return this.newPosition;
        }

        @Nonnull
        public String value()
        {
            return this.value;
        }

        @Nullable
        public ChangeType type()
        {
            return this.type;
        }

        @Nonnull
        public static SectionItem addedItem(int newPosition,
                                            @Nonnull String value)
        {
            return new SectionItem(null, newPosition, value, ChangeType.ADDITION);
        }

        @Nonnull
        public static SectionItem deletedItem(int oldPosition,
                                              @Nonnull String value)
        {
            return new SectionItem(oldPosition, null, value, ChangeType.DELETION);
        }

        @Nonnull
        public static SectionItem item(int oldPosition,
                                       int newPosition,
                                       @Nonnull String value)
        {
            return new SectionItem(oldPosition, newPosition, value, null);
        }

    }


}
