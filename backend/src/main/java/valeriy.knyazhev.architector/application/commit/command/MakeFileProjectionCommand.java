package valeriy.knyazhev.architector.application.commit.command;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class MakeFileProjectionCommand
{

    @Nonnull
    private ProjectId projectId;

    @Nonnull
    private FileId fileId;

    private long commitId;

    public MakeFileProjectionCommand(@Nonnull String projectId,
                                     @Nonnull String fileId,
                                     long commitId)
    {
        this.projectId = ProjectId.of(Args.notBlank(projectId, "Project identifier is required."));
        this.fileId = FileId.of(Args.notBlank(fileId, "File identifier is required."));
        this.commitId = commitId;
    }

    @Nonnull
    public ProjectId projectId()
    {
        return this.projectId;
    }

    @Nonnull
    public FileId fileId()
    {
        return this.fileId;
    }

    public long commitId()
    {
        return this.commitId;
    }

}
