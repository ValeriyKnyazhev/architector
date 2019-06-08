package valeriy.knyazhev.architector.domain.model.project;

import lombok.NoArgsConstructor;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileContent;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static lombok.AccessLevel.PROTECTED;
import static valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights.*;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Entity
@EntityListeners(ProjectEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@Table(name = "projects")
public class Project
{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projectIdGenerator")
    @SequenceGenerator(name = "projectIdGenerator", sequenceName = "project_id_seq", allocationSize = 1)
    private long id;

    @AttributeOverride(name = "id", column = @Column(name = "project_id", nullable = false, updatable = false))
    @Embedded
    @Nonnull
    private ProjectId projectId;

    @Nonnull
    private LocalDateTime createdDate;

    @Nonnull
    private LocalDateTime updatedDate;

    @Nonnull
    private String name;

    @Nonnull
    private String author;

    @Nonnull
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "project_id", nullable = false)
    @OrderColumn(name = "file_order")
    @Nonnull
    private List<File> files = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "project_read_access_rights",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "architector_id")
    )
    private Set<Architector> readAccessRights;

    @ManyToMany
    @JoinTable(
        name = "project_write_access_rights",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "architector_id")
    )
    private Set<Architector> writeAccessRights;

    @Nullable
    private Long currentCommitId;

    @Nonnull
    @Version
    private long concurrencyVersion;

    private Project(@Nonnull ProjectId projectId,
                    @Nonnull String name,
                    @Nonnull String author,
                    @Nonnull String description,
                    @Nonnull List<File> files)
    {
        this.projectId = projectId;
        this.name = name;
        this.author = author;
        this.description = description;
        this.files = files;
    }

    @Nonnull
    public ProjectId projectId()
    {
        return this.projectId;
    }

    @Nonnull
    public String name()
    {
        return this.name;
    }

    @Nonnull
    public String author()
    {
        return this.author;
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
        return Collections.unmodifiableList(this.files);
    }

    @Nullable
    public Long currentCommitId()
    {
        return this.currentCommitId;
    }

    @Nonnull
    public Set<Architector> writeAccessRights()
    {
        return Collections.unmodifiableSet(this.writeAccessRights);
    }

    @Nonnull
    public Set<Architector> readAccessRights()
    {
        return Collections.unmodifiableSet(this.readAccessRights);
    }

    public boolean canBeUpdated(@Nonnull Architector architector)
    {
        Args.notNull(architector, "Architector is required.");
        ProjectAccessRights accessRights = defineAccessRightsFor(architector);
        return accessRights.canBeUpdated();
    }

    public boolean canBeRead(@Nonnull Architector architector)
    {
        Args.notNull(architector, "Architector is required.");
        ProjectAccessRights accessRights = defineAccessRightsFor(architector);
        return accessRights.canBeRead();
    }

    @Nonnull
    public ProjectAccessRights accessRights(@Nonnull Architector architector)
    {
        Args.notNull(architector, "Architector is required.");
        return defineAccessRightsFor(architector);
    }

    @Nonnull
    private ProjectAccessRights defineAccessRightsFor(@Nonnull Architector architector)
    {
        if (isOwner(architector))
        {
            return OWNER;
        }
        if (architector.isAdmin())
        {
            return WRITE;
        }
        if (this.writeAccessRights.stream()
            .anyMatch(architector::equals))
        {
            return WRITE;
        }
        if (this.readAccessRights.stream()
            .anyMatch(architector::equals))
        {
            return READ;
        }
        return FORBIDDEN;
    }

    private boolean isOwner(@Nonnull Architector architector)
    {
        return Args.notNull(architector, "Architector is required.").email().equals(this.author);
    }

    public void addWriteAccessRights(@Nonnull Architector architector)
    {
        this.readAccessRights.remove(architector);
        this.writeAccessRights.add(architector);
    }

    public void addReadAccessRights(@Nonnull Architector architector)
    {
        if (!this.writeAccessRights.contains(architector))
        {
            this.readAccessRights.add(architector);
        }
    }

    public void takeAwayAccessRights(@Nonnull Architector architector)
    {
        this.readAccessRights.remove(architector);
        this.writeAccessRights.remove(architector);
    }

    public void updateCurrentCommitId(long commitId)
    {
        this.currentCommitId = commitId;
    }

    public void addFile(@Nonnull File file)
    {
        this.files.add(file);
    }

    public void updateFile(@Nonnull FileId fileId, @Nonnull FileContent content)
    {
        this.files.stream()
            .filter(f -> fileId.equals(f.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId))
            .updateContent(content);
    }

    @Nonnull
    public File deleteFile(@Nonnull FileId fileId)
    {
        File deleted = this.files.stream()
            .filter(f -> fileId.equals(f.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId));
        this.files.remove(deleted);
        return deleted;
    }

    public boolean updateName(@Nonnull String name)
    {
        Args.notBlank(name, "Project name is required.");
        if (name.equals(this.name))
        {
            return false;
        } else
        {
            this.name = name;
            return true;
        }
    }

    public boolean updateDescription(@Nonnull String description)
    {
        Args.notNull(description, "Project description is required.");
        if (description.equals(this.description))
        {
            return false;
        } else
        {
            this.description = description;
            return true;
        }
    }

    void setCreatedDate(@Nonnull LocalDateTime date)
    {
        this.createdDate = date;
    }

    void setUpdatedDate(@Nonnull LocalDateTime date)
    {
        this.updatedDate = date;
    }

    @Nonnull
    public static ProjectConstructor constructor()
    {
        return new ProjectConstructor();
    }

    public static class ProjectConstructor
    {

        private ProjectId projectId;

        private String projectName;

        private String author;

        private String description;

        private File file;

        ProjectConstructor()
        {
        }

        @Nonnull
        public ProjectConstructor projectId(@Nonnull ProjectId projectId)
        {
            this.projectId = projectId;
            return this;
        }

        @Nonnull
        public ProjectConstructor withName(@Nonnull String projectName)
        {
            this.projectName = projectName;
            return this;
        }

        @Nonnull
        public ProjectConstructor withAuthor(@Nonnull String author)
        {
            this.author = author;
            return this;
        }

        @Nonnull
        public ProjectConstructor withDescription(@Nonnull String description)
        {
            this.description = description;
            return this;
        }

        @Nonnull
        public ProjectConstructor withFile(@Nonnull File file)
        {
            this.file = file;
            return this;
        }

        @Nonnull
        public Project construct()
        {
            return new Project(
                this.projectId, this.projectName, this.author, this.description, singletonList(this.file)
            );
        }

    }

}
