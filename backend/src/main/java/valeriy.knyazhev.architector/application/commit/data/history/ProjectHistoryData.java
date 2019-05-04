package valeriy.knyazhev.architector.application.commit.data.history;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.commit.Commit;

import javax.annotation.Nonnull;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class ProjectHistoryData extends AbstractHistoryData
{

    @Nonnull
    private final String projectId;

    public ProjectHistoryData(@Nonnull String projectId,
                              @Nonnull List<Commit> commits)
    {
        super(commits);
        this.projectId = Args.notBlank(projectId, "Project identifier is required.");
    }

}
