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

    @Nonnull
    private final String name;

    @Nonnull
    private final String description;

    @Nonnull
    private final List<FileChangesData> changedFiles;

    public CommitChangesData(@Nonnull String name,
                             @Nonnull String description,
                             @Nonnull List<FileChangesData> files)
    {
        this.name = Args.notBlank(name, "Project name is required.");
        this.description = Args.notNull(description, "Project description is required.");
        this.changedFiles = Args.notNull(files, "Changed files are required.");
    }

}
