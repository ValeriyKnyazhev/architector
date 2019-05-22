package valeriy.knyazhev.architector.port.adapter.resources.project.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.application.project.AccessGrantedInfo;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights;
import valeriy.knyazhev.architector.domain.model.util.serialization.ArchitectorLocalDateTimeSerializer;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.FileBriefModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @Nullable
    private AccessGrantedInfo accessGrantedInfo;

    @Nonnull
    private String projectName;

    @Nonnull
    private String description;

    @Nonnull
    private List<FileBriefModel> files;

    public ProjectDescriptorModel(@Nonnull String projectId, @Nonnull LocalDateTime createdDate,
                                  @Nonnull LocalDateTime updatedDate, @Nonnull ProjectAccessRights accessRights,
                                  @Nullable AccessGrantedInfo accessGrantedInfo,
                                  @Nonnull String projectName, @Nonnull String description,
                                  @Nonnull String author, @Nonnull List<FileBriefModel> files)
    {
        this.projectId = Args.notBlank(projectId, "Project identifier is required.");
        this.createdDate = Args.notNull(createdDate, "Project created date is required.");
        this.updatedDate = Args.notNull(updatedDate, "Project updated date is required.");
        this.accessRights = Args.notNull(accessRights, "Project access rights is required.");
        this.accessGrantedInfo = accessGrantedInfo;
        this.projectName = Args.notBlank(projectName, "Project name is required.");
        this.description = Args.notNull(description, "Project description is required.");
        this.author = Args.notBlank(author, "Project author is required.");
        this.files = Args.notNull(files, "Project files are required.");
    }

}
