package valeriy.knyazhev.architector.application.project.file.conflict.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Valeriy Knyazhev
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ConflictChange
{

    @Nonnull
    private Object oldValue;

    @Nullable
    private Object headValue;

    @Nullable
    private Object newValue;

    private ConflictChange(@Nonnull Object oldValue,
                           @Nullable Object headValue,
                           @Nullable Object newValue)
    {
        this.oldValue = oldValue;
        this.headValue = headValue;
        this.newValue = newValue;
    }

    public boolean hasConflict()
    {
        return this.headValue != null || this.newValue != null;
    }

    @Nonnull
    public Object oldValue()
    {
        return this.oldValue;
    }

    @Nullable
    public Object headValue()
    {
        return this.headValue;
    }

    @Nullable
    public Object newValue()
    {
        return this.newValue;
    }

    public static ConflictChange withConflict(@Nonnull String oldValue,
                                              @Nullable String headValue,
                                              @Nullable String newValue)
    {
        return new ConflictChange(oldValue, headValue, newValue);
    }

    public static ConflictChange withConflict(@Nonnull List<String> oldValue,
                                              @Nullable List<String> headValue,
                                              @Nullable List<String> newValue)
    {
        return new ConflictChange(oldValue, headValue, newValue);
    }

    public static ConflictChange withConflict(@Nonnull LocalDateTime oldValue,
                                              @Nullable LocalDateTime headValue,
                                              @Nullable LocalDateTime newValue)
    {
        return new ConflictChange(oldValue, headValue, newValue);
    }

    public static ConflictChange oldValue(@Nonnull String oldValue)
    {
        return new ConflictChange(oldValue, null, null);
    }

    public static ConflictChange oldValue(@Nonnull List<String> oldValue)
    {
        return new ConflictChange(oldValue, null, null);
    }

    public static ConflictChange oldValue(@Nonnull LocalDateTime oldValue)
    {
        return new ConflictChange(oldValue, null, null);
    }

}