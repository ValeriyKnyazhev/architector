package valeriy.knyazhev.architector.application.project;

import lombok.Builder;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Valeriy Knyazhev
 */
public class ProjectData
{

    @Nonnull
    private String projectId;

    @Nonnull
    private String author;

    @Nonnull
    private String name;

    @Nonnull
    private String description;

    @Nonnull
    private LocalDateTime createdDate;

    @Nonnull
    private LocalDateTime updatedDate;

    @Nonnull
    private List<File> files;

    @Nonnull
    private ProjectAccessRights accessRights;

    @Nullable
    private AccessGrantedInfo accessGrantedInfo;

    @Builder
    private ProjectData(@Nonnull String projectId, @Nonnull String author, @Nonnull String name,
                        @Nonnull String description, @Nonnull LocalDateTime createdDate,
                        @Nonnull LocalDateTime updatedDate, @Nonnull List<File> files,
                        @Nonnull ProjectAccessRights accessRights, @Nullable AccessGrantedInfo accessGrantedInfo)
    {
        this.projectId = Args.notBlank(projectId, "Project identifier is required.");
        this.author = Args.notBlank(author, "Project author is required.");
        this.name = Args.notBlank(name, "Project name is required.");
        this.description = Args.notNull(description, "Project description is required.");
        this.createdDate = Args.notNull(createdDate, "Project created date is required.");
        this.updatedDate = Args.notNull(updatedDate, "Project updated date is required.");
        this.files = Args.notNull(files, "Project files is required.");
        this.accessRights = Args.notNull(accessRights, "Project access rights is required.");
        this.accessGrantedInfo = accessGrantedInfo;
    }

    @Nonnull
    public String projectId()
    {
        return this.projectId;
    }

    @Nonnull
    public String author()
    {
        return this.author;
    }

    @Nonnull
    public String name()
    {
        return this.name;
    }

    @Nonnull
    public String description()
    {
        return this.description;
    }

    @Nonnull
    public LocalDateTime createdDate()
    {
        return this.createdDate;
    }

    @Nonnull
    public LocalDateTime updatedDate()
    {
        return this.updatedDate;
    }

    @Nonnull
    public List<File> files()
    {
        return this.files;
    }

    @Nonnull
    public ProjectAccessRights accessRights()
    {
        return this.accessRights;
    }

    @Nullable
    public AccessGrantedInfo accessGrantedInfo()
    {
        return this.accessGrantedInfo;
    }



}
