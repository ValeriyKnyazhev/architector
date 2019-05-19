package valeriy.knyazhev.architector.port.adapter.resources.project.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights;
import valeriy.knyazhev.architector.domain.model.util.serialization.ArchitectorLocalDateTimeSerializer;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.FileBriefModel;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class ProjectDescriptorModel
{


    @Nonnull
    private String projectId;

    @Nonnull
    @JsonSerialize(using = ArchitectorLocalDateTimeSerializer.class)
    private LocalDateTime createdDate;

    @Nonnull
    @JsonSerialize(using = ArchitectorLocalDateTimeSerializer.class)
    private LocalDateTime updatedDate;

    @Nonnull
    private String author;

    @Nonnull
    private ProjectAccessRights accessRights;

    @Nonnull
    private String projectName;

    @Nonnull
    private String description;

    @Nonnull
    private List<FileBriefModel> files;

    public ProjectDescriptorModel(@Nonnull String projectId, @Nonnull LocalDateTime createdDate,
                                  @Nonnull LocalDateTime updatedDate, @Nonnull ProjectAccessRights accessRights,
                                  @Nonnull String projectName, @Nonnull String description,
                                  @Nonnull String author, @Nonnull List<FileBriefModel> files)
    {
        this.projectId = projectId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.accessRights = accessRights;
        this.projectName = projectName;
        this.description = description;
        this.author = author;
        this.files = files;
    }

}
