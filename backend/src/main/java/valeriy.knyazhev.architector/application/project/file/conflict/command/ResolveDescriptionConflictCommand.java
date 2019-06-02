package valeriy.knyazhev.architector.application.project.file.conflict.command;

import lombok.Builder;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class ResolveDescriptionConflictCommand
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
    private List<String> descriptions;

    @Nonnull
    private String implementationLevel;

    @Builder
    public ResolveDescriptionConflictCommand(@Nonnull String projectId,
                                             @Nonnull String fileId,
                                             @Nonnull Architector architector,
                                             @Nonnull Long headCommitId,
                                             @Nonnull List<String> descriptions,
                                             @Nonnull String implementationLevel)
    {
        this.projectId = projectId;
        this.fileId = fileId;
        this.architector = architector;
        this.descriptions = descriptions;
        this.implementationLevel = implementationLevel;
        this.headCommitId = headCommitId;
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

    @Nonnull
    public FileDescription fixedDescription()
    {
        return FileDescription.of(
            this.descriptions, this.implementationLevel
        );
    }

    public long headCommitId()
    {
        return this.headCommitId;
    }

}
