package valeriy.knyazhev.architector.application.project.file.conflict.command;

import lombok.Builder;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class ResolveContentConflictCommand
{

    @Nonnull
    private String projectId;

    @Nonnull
    private String fileId;

    @Nonnull
    private Architector architector;

    @Nonnull
    private Long headCommitId;

    @Nonnull
    private List<String> contentItems;

    @Builder
    public ResolveContentConflictCommand(@Nonnull String projectId,
                                         @Nonnull String fileId,
                                         @Nonnull Architector architector,
                                         @Nonnull Long headCommitId,
                                         @Nonnull String content)
    {
        this.projectId = projectId;
        this.fileId = fileId;
        this.architector = architector;
        this.headCommitId = headCommitId;
        this.contentItems = asList(content.split(System.lineSeparator()));;
    }

    @Nonnull
    public ProjectId projectId()
    {
        return ProjectId.of(this.projectId);
    }

    @Nonnull
    public FileId fileId()
    {
        return FileId.of(this.fileId);
    }

    @Nonnull
    public Architector architector()
    {
        return this.architector;
    }

    public long headCommitId()
    {
        return this.headCommitId;
    }

    @Nonnull
    public List<String> contentItems()
    {
        return this.contentItems;
    }

}
