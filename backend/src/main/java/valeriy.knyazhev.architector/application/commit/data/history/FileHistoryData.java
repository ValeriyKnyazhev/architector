package valeriy.knyazhev.architector.application.commit.data.history;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.commit.Commit;

import javax.annotation.Nonnull;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class FileHistoryData extends AbstractHistoryData
{

    @Nonnull
    private String fileId;

    public FileHistoryData(@Nonnull String fileId,
                           @Nonnull List<Commit> commits)
    {
        super(commits);
        this.fileId = Args.notBlank(fileId, "File identifier is required.");
    }

}