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

    @Nonnull
    // FIXME now the number of files is 1
    private List<CommitFileItem> changedFiles;

    @Builder
    private CommitDescription(@Nonnull List<CommitFileItem> files)
    {
        this.changedFiles = Args.notNull(files, "Files are required.");
        if (files.isEmpty())
        {
            throw new NothingToCommitException();
        }
    }

    protected CommitDescription()
    {
        // empty
    }

    @Nonnull
    public List<CommitFileItem> changedFiles()
    {
        return this.changedFiles;
    }

}