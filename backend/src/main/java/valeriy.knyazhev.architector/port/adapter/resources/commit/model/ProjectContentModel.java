package valeriy.knyazhev.architector.port.adapter.resources.commit.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.FileContentModel;

import javax.annotation.Nonnull;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class ProjectContentModel
{

    @Nonnull
    private String projectId;

    @Nonnull
    private String projectName;

    @Nonnull
    private List<FileContentModel> files;

    public ProjectContentModel(@Nonnull String projectId,
                               @Nonnull String projectName,
                               @Nonnull List<FileContentModel> files)
    {
        this.projectId = projectId;
        this.projectName = projectName;
        this.files = files;
    }

}