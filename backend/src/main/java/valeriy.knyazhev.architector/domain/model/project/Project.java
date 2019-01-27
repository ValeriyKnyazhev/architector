package valeriy.knyazhev.architector.domain.model.project;

import lombok.NoArgsConstructor;
import org.bimserver.emf.Schema;
import valeriy.knyazhev.architector.domain.model.project.file.File;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static javax.persistence.GenerationType.TABLE;
import static lombok.AccessLevel.PROTECTED;
import static org.bimserver.emf.Schema.IFC2X3TC1;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Entity
@EntityListeners(ProjectEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@Table(name = "projects")
public class Project {


    @Id
    @GeneratedValue(strategy = TABLE)
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
    @Enumerated(EnumType.STRING)
    private Schema schema = IFC2X3TC1;

    @AttributeOverrides({
            @AttributeOverride(name = "descriptions",
                    column = @Column(name = "descriptions")),
            @AttributeOverride(name = "implementationLevel",
                    column = @Column(name = "implementation_level"))
    })
    @Embedded
    @Nonnull
    private ProjectDescription description;

    @AttributeOverrides({
            @AttributeOverride(name = "name",
                    column = @Column(name = "name")),
            @AttributeOverride(name = "timestamp",
                    column = @Column(name = "timestamp")),
            @AttributeOverride(name = "authors",
                    column = @Column(name = "authors")),
            @AttributeOverride(name = "organizations",
                    column = @Column(name = "organizations")),
            @AttributeOverride(name = "preprocessorVersion",
                    column = @Column(name = "preprocessor_version")),
            @AttributeOverride(name = "originatingSystem",
                    column = @Column(name = "originating_system")),
            @AttributeOverride(name = "authorisation",
                    column = @Column(name = "authorisation"))
    })
    @Embedded
    @Nonnull
    private ProjectMetadata metadata;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    @OrderColumn(name = "file_order")
    @Nonnull
    private List<File> files = new ArrayList<>();

    @Nonnull
    @Version
    private long concurrencyVersion;

    private Project(@Nonnull ProjectId projectId,
                    @Nonnull ProjectDescription description,
                    @Nonnull ProjectMetadata metadata,
                    @Nonnull List<File> files) {
        this.projectId = projectId;
        this.description = description;
        this.metadata = metadata;
        this.files = files;
    }

    @Nonnull
    public static ProjectConstructor constructor() {
        return new ProjectConstructor();
    }

    @Nonnull
    public ProjectId projectId() {
        return this.projectId;
    }

    @Nonnull
    public LocalDateTime createdDate() {
        return this.createdDate;
    }

    @Nonnull
    public LocalDateTime updatedDate() {
        return this.updatedDate;
    }

    @Nonnull
    public Schema schema() {
        return this.schema;
    }

    @Nonnull
    public ProjectDescription description() {
        return this.description;
    }

    @Nonnull
    public ProjectMetadata metadata() {
        return this.metadata;
    }

    @Nonnull
    public List<File> files() {
        return this.files;
    }

    public void updateDescription(@Nonnull ProjectDescription description) {
        this.description = description;
    }

    public void updateMetadata(@Nonnull ProjectMetadata metadata) {
        this.metadata = metadata;
    }

    public void addFile(@Nonnull File file) {
        this.files.add(file);
    }

    void setCreatedDate(@Nonnull LocalDateTime date) {
        this.createdDate = date;
    }

    void setUpdatedDate(@Nonnull LocalDateTime date) {
        this.updatedDate = date;
    }

    public static class ProjectConstructor {

        private ProjectId projectId;

        private ProjectDescription description;

        private ProjectMetadata metadata;

        private File file;

        ProjectConstructor() {
        }

        @Nonnull
        public ProjectConstructor projectId(@Nonnull ProjectId projectId) {
            this.projectId = projectId;
            return this;
        }

        @Nonnull
        public ProjectConstructor withDescription(@Nonnull ProjectDescription description) {
            this.description = description;
            return this;
        }

        @Nonnull
        public ProjectConstructor withMetadata(@Nonnull ProjectMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        @Nonnull
        public ProjectConstructor withFile(@Nonnull File file) {
            this.file = file;
            return this;
        }

        @Nonnull
        public Project construct() {
            return new Project(this.projectId, this.description, this.metadata, singletonList(this.file));
        }

    }

}
