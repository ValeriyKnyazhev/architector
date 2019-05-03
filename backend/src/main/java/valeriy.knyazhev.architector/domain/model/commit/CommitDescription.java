package valeriy.knyazhev.architector.domain.model.commit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import org.apache.http.util.Args;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class CommitDescription implements Serializable
{

    @Nullable
    private String name;

    @Nullable
    private String description;

    @Nonnull
    // FIXME now the number of files is 1
    private List<CommitFileItem> changedFiles;

    @Builder
    private CommitDescription(@Nullable String name,
                              @Nullable String description,
                              @Nonnull List<CommitFileItem> files)
    {
        this.name = name;
        this.description = description;
        this.changedFiles = Args.notNull(files, "Files are required.");
        Args.check(name != null || description != null || !files.isEmpty(),
            "Project changes must not be empty.");
    }

    protected CommitDescription()
    {
        // empty
    }

    @Nullable
    public String name()
    {
        return this.name;
    }

    @Nonnull
    public String newName(@Nonnull String oldValue)
    {
        return this.name != null ? this.name : oldValue;
    }

    @Nullable
    public String description()
    {
        return this.description;
    }

    @Nonnull
    public String newDescription(@Nonnull String oldValue)
    {
        return this.description != null ? this.description : oldValue;
    }

    @Nonnull
    public List<CommitFileItem> changedFiles()
    {
        return this.changedFiles;
    }

}