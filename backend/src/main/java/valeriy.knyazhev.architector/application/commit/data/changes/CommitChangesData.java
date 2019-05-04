package valeriy.knyazhev.architector.application.commit.data.changes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.http.util.Args;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class CommitChangesData
{

    @Nullable
    private final ChangedValue<String> name;

    @Nullable
    private final ChangedValue<String> description;

    @Nonnull
    private final List<FileChangesData> changedFiles;

    public CommitChangesData(@Nullable ChangedValue<String> name,
                             @Nullable ChangedValue<String> description,
                             @Nonnull List<FileChangesData> files)
    {
        this.name = name;
        this.description = description;
        this.changedFiles = Args.notNull(files, "Changed files are required.");
    }

}
