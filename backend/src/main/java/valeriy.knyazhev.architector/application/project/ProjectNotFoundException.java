package valeriy.knyazhev.architector.application.project;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
public class ProjectNotFoundException extends IllegalStateException
{

    public ProjectNotFoundException(@Nonnull ProjectId projectId)
    {
        super(
            "Unable to find project with identifier " +
            Args.notNull(projectId, "Project identifier is required.").id() + "."
        );
    }

}
