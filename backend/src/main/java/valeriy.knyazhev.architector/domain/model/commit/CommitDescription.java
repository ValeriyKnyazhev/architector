package valeriy.knyazhev.architector.domain.model.commit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.annotation.Nonnull;
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

    private CommitDescription(@Nonnull List<CommitFileItem> files)
    {
        this.changedFiles = files;
    }

    protected CommitDescription()
    {
        // empty
    }

    @Nonnull
    public static CommitDescription of(@Nonnull List<CommitFileItem> files)
    {
        return new CommitDescription(files);
    }

    @Nonnull
    public List<CommitFileItem> changedFiles()
    {
        return this.changedFiles;
    }

}