package valeriy.knyazhev.architector.application.commit.data.changes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.http.util.Args;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ChangedValue<T>
{

    @Nullable
    private final T oldValue;

    @Nonnull
    private final T newValue;

    private ChangedValue(@Nullable T oldValue, @Nonnull T newValue)
    {
        this.oldValue = oldValue;
        this.newValue = Args.notNull(newValue, "New value is required.");
    }

    @Nonnull
    public static <T> ChangedValue<T> newValue(@Nonnull T newValue)
    {
        Args.notNull(newValue, "New value is required.");
        return new ChangedValue<>(null, newValue);
    }

    @Nonnull
    public static <T> ChangedValue<T> changeValue(@Nonnull T oldValue,
                                                  @Nonnull T newValue)
    {
        Args.notNull(oldValue, "Old value is required.");
        Args.notNull(newValue, "New value is required.");
        return new ChangedValue<>(oldValue, newValue);
    }

}
