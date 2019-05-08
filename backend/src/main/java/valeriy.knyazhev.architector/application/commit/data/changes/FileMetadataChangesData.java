package valeriy.knyazhev.architector.application.commit.data.changes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FileMetadataChangesData
{

    @Nullable
    private final ChangedValue<String> name;

    @Nullable
    private final ChangedValue<String> timestamp;

    @Nullable
    private final ChangedValue<List<String>> authors;

    @Nullable
    private final ChangedValue<List<String>> organizations;

    @Nullable
    private final ChangedValue<String> preprocessorVersion;

    @Nullable
    private final ChangedValue<String> originatingSystem;

    @Nullable
    private final ChangedValue<String> authorization;

    @Builder
    private FileMetadataChangesData(@Nullable ChangedValue<String> name,
                                    @Nullable ChangedValue<String> timestamp,
                                    @Nullable ChangedValue<List<String>> authors,
                                    @Nullable ChangedValue<List<String>> organizations,
                                    @Nullable ChangedValue<String> preprocessorVersion,
                                    @Nullable ChangedValue<String> originatingSystem,
                                    @Nullable ChangedValue<String> authorization)
    {
        this.name = name;
        this.timestamp = timestamp;
        this.authors = authors;
        this.organizations = organizations;
        this.preprocessorVersion = preprocessorVersion;
        this.originatingSystem = originatingSystem;
        this.authorization = authorization;
    }

}
