package valeriy.knyazhev.architector.application.commit.data;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.project.commit.Commit;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
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
