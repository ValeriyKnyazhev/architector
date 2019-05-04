package valeriy.knyazhev.architector.application.commit.data.changes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FileDescriptionChangesData
{

    @Nullable
    private final ChangedValue<List<String>> descriptions;

    @Nullable
    private final ChangedValue<String> implementationLevel;

    @Builder
    private FileDescriptionChangesData(@Nullable ChangedValue<List<String>> descriptions,
                                       @Nullable ChangedValue<String> implementationLevel)
    {
        this.descriptions = descriptions;
        this.implementationLevel = implementationLevel;
    }

}
