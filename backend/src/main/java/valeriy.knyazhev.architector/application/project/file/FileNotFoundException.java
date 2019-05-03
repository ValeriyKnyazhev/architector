package valeriy.knyazhev.architector.application.project.file;

import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
public class FileNotFoundException extends IllegalStateException
{

    public FileNotFoundException(@Nonnull ProjectId projectId, @Nonnull FileId fileId)
    {
        super("Unable to find file with id " +
              Args.notNull(fileId, "File identifier is required.").id() + " in project with id " +
              Args.notNull(projectId, "Project identifier is required.").id() + ".");
    }

}
