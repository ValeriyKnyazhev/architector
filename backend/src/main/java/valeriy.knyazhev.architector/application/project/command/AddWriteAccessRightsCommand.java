package valeriy.knyazhev.architector.application.project.command;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class AddWriteAccessRightsCommand
{

    @Nonnull
    private String projectId;

    @Nonnull
    private String architector;

    public AddWriteAccessRightsCommand(@Nonnull String projectId,
                                       @Nonnull String architector)
    {
        this.projectId = Args.notBlank(projectId, "Project identifier is required.");
        this.architector = Args.notBlank(architector, "Architector is required.");
    }

    @Nonnull
    public ProjectId projectId()
    {
        return ProjectId.of(this.projectId);
    }

    @Nonnull
    public String architector()
    {
        return this.architector;
    }

}
