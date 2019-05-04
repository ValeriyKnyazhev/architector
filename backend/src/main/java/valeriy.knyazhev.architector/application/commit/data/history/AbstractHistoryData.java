package valeriy.knyazhev.architector.application.commit.data.history;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.application.commit.data.CommitDescriptorData;
import valeriy.knyazhev.architector.domain.model.commit.Commit;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Valeriy Knyazhev
 */
public abstract class AbstractHistoryData
{

    @Nonnull
    private final List<CommitDescriptorData> commits;

    public AbstractHistoryData(@Nonnull List<Commit> commits)
    {
        this.commits = Args.notNull(commits, "Commits are required.")
            .stream()
            .map(AbstractHistoryData::constructDescription)
            .collect(Collectors.toList());
    }

    @Nonnull
    public List<CommitDescriptorData> commits()
    {
        return Collections.unmodifiableList(this.commits);
    }

    @Nonnull
    private static CommitDescriptorData constructDescription(@Nonnull Commit commit)
    {
        return new CommitDescriptorData(
            commit.id(),
            commit.parentId(),
            commit.author(),
            commit.message(),
            commit.timestamp()
        );
    }

}