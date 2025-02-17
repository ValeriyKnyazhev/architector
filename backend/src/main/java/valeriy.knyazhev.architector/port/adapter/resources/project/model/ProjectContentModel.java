package valeriy.knyazhev.architector.port.adapter.resources.project.model;

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
    private String name;

    @Nonnull
    private String description;

    @Nonnull
    private List<FileContentModel> files;

    public ProjectContentModel(@Nonnull String projectId,
                               @Nonnull String name,
                               @Nonnull String description,
                               @Nonnull List<FileContentModel> files)
    {
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.files = files;
    }

}