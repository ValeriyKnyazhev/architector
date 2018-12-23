package valeriy.knyazhev.architector.domain.model.project;

import lombok.NoArgsConstructor;
import valeriy.knyazhev.architector.domain.model.project.file.File;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.TABLE;
import static lombok.AccessLevel.PROTECTED;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    @OrderColumn(name = "file_order")
    @Nonnull
    private List<File> files = new ArrayList<>();

    @Nonnull
    @Version
    private long concurrencyVersion;

    private Project(@Nonnull ProjectId projectId, @Nonnull List<File> files) {
        this.projectId = projectId;
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
    public List<File> files() {
        return this.files;
    }

    public void addFile(@Nonnull File file) {
        this.files.add(file);
    }

    public void removeFile(@Nonnull File file) {
        this.files.remove(file);
    }

    void setCreatedDate(@Nonnull LocalDateTime date) {
        this.createdDate = date;
    }

    void setUpdatedDate(@Nonnull LocalDateTime date) {
        this.updatedDate = date;
    }

    public static class ProjectConstructor {

        private ProjectId projectId;

        private List<File> files = new ArrayList<>();

        ProjectConstructor() {
        }

        public ProjectConstructor projectId(@Nonnull ProjectId projectId) {
            this.projectId = projectId;
            return this;
        }

        public ProjectConstructor withFiles(@Nonnull List<File> files) {
            this.files.addAll(files);
            return this;
        }

        public ProjectConstructor withFile(@Nonnull File file) {
            this.files.add(file);
            return this;
        }

        public Project construct() {
            return new Project(projectId, files);
        }

    }

}
