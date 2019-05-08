package valeriy.knyazhev.architector.application.commit.command;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class FetchCommitChangesCommand
{

    @Nonnull
    private ProjectId projectId;

    private long commitId;

    public FetchCommitChangesCommand(@Nonnull String projectId,
                                     long commitId)
    {
        this.projectId = ProjectId.of(Args.notBlank(projectId, "Project identifier is required."));
        this.commitId = commitId;
    }

    @Nonnull
    public ProjectId projectId()
    {
        return this.projectId;
    }

    public long commitId()
    {
        return this.commitId;
    }

}
