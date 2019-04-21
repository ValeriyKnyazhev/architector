package valeriy.knyazhev.architector.domain.model.project;

import lombok.NoArgsConstructor;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileContent;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
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

    @Nonnull
    private String name;

    @Nonnull
    private String author;

    @Nonnull
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    @OrderColumn(name = "file_order")
    @Nonnull
    private List<File> files = new ArrayList<>();

    @Nonnull
    @Version
    private long concurrencyVersion;

    private Project(@Nonnull ProjectId projectId,
                    @Nonnull String name,
                    @Nonnull String author,
                    @Nonnull String description,
                    @Nonnull List<File> files) {
        this.projectId = projectId;
        this.name = name;
        this.author = author;
        this.description = description;
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
    public String name() {
        return this.name;
    }

    @Nonnull
    public String author() {
        return this.author;
    }

    @Nonnull
    public String description() {
        return this.description;
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
        if (!this.files.isEmpty()) {
            // FIXME now project must have only one file
            throw new IllegalStateException("Unable to save a few file in one project.");
        }
        this.files.add(file);
    }

    public void updateFile(@Nonnull FileId fileId, @Nonnull FileContent content) {
        this.files.stream()
            .filter(f -> fileId.equals(f.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId))
            .updateContent(content);
    }

    void setCreatedDate(@Nonnull LocalDateTime date) {
        this.createdDate = date;
    }

    void setUpdatedDate(@Nonnull LocalDateTime date) {
        this.updatedDate = date;
    }

    public static class ProjectConstructor {

        private ProjectId projectId;

        private String projectName;

        private String author;

        private String description;

        private File file;

        ProjectConstructor() {
        }

        @Nonnull
        public ProjectConstructor projectId(@Nonnull ProjectId projectId) {
            this.projectId = projectId;
            return this;
        }

        @Nonnull
        public ProjectConstructor withName(@Nonnull String projectName) {
            this.projectName = projectName;
            return this;
        }

        @Nonnull
        public ProjectConstructor withAuthor(@Nonnull String author) {
            this.author = author;
            return this;
        }

        @Nonnull
        public ProjectConstructor withDescription(@Nonnull String description) {
            this.description = description;
            return this;
        }

        @Nonnull
        public ProjectConstructor withFile(@Nonnull File file) {
            this.file = file;
            return this;
        }

        @Nonnull
        public Project construct() {
            return new Project(
                this.projectId, this.projectName, this.author, this.description, singletonList(this.file)
            );
        }

    }

}
