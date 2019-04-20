package valeriy.knyazhev.architector.domain.model.project.file;

import lombok.NoArgsConstructor;
import org.bimserver.emf.Schema;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import valeriy.knyazhev.architector.domain.model.project.file.FileContent.FileContentJsonbType;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.TABLE;
import static lombok.AccessLevel.PROTECTED;
import static org.bimserver.emf.Schema.IFC2X3TC1;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Entity
@EntityListeners(FileEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@Table(name = "files")
@TypeDef(typeClass = FileContentJsonbType.class, name = "file_content_jsonb")
public class File {


    @Id
    @GeneratedValue(strategy = TABLE)
    private long id;

    @AttributeOverride(name = "id", column = @Column(name = "file_id", nullable = false, updatable = false))
    @Embedded
    @Nonnull
    private FileId fileId;

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

    @Nonnull
    @Column(columnDefinition = "jsonb")
    @Type(type = "file_content_jsonb")
    private FileContent content;

    @Nonnull
    @Version
    private long concurrencyVersion;

    private File(@Nonnull FileId fileId,
                 @Nonnull ProjectDescription description,
                 @Nonnull ProjectMetadata metadata,
                 @Nonnull FileContent content) {
        this.fileId = fileId;
        this.description = description;
        this.metadata = metadata;
        this.content = content;
    }

    @Nonnull
    public static FileConstructor constructor() {
        return new FileConstructor();
    }

    @Nonnull
    public FileId fileId() {
        return this.fileId;
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
    public FileContent content() {
        return this.content;
    }

    @Nonnull
    public LocalDateTime createdDate() {
        return this.createdDate;
    }

    @Nonnull
    public LocalDateTime updatedDate() {
        return this.updatedDate;
    }

    public void updateDescription(@Nonnull ProjectDescription description) {
        this.description = description;
    }

    public void updateMetadata(@Nonnull ProjectMetadata metadata) {
        this.metadata = metadata;
    }

    public void updateContent(@Nonnull FileContent content) {
        this.content = content;
    }

    void setCreatedDate(@Nonnull LocalDateTime date) {
        this.createdDate = date;
    }

    void setUpdatedDate(@Nonnull LocalDateTime date) {
        this.updatedDate = date;
    }

    public static class FileConstructor {

        private FileId fileId;

        private ProjectDescription description;

        private ProjectMetadata metadata;

        private FileContent content;

        FileConstructor() {
        }

        @Nonnull
        public FileConstructor fileId(@Nonnull FileId fileId) {
            this.fileId = fileId;
            return this;
        }

        @Nonnull
        public FileConstructor withDescription(@Nonnull ProjectDescription description) {
            this.description = description;
            return this;
        }

        @Nonnull
        public FileConstructor withMetadata(@Nonnull ProjectMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        @Nonnull
        public FileConstructor withContent(@Nonnull FileContent content) {
            this.content = content;
            return this;
        }

        @Nonnull
        public File construct() {
            return new File(this.fileId, this.description, this.metadata, this.content);
        }

    }

}
