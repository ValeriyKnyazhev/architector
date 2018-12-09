package valeriy.knyazhev.architector.domain.model.project.file;

import lombok.Builder;
import lombok.NoArgsConstructor;
import org.bimserver.emf.Schema;

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
    private FileDescription description;

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
    private FileMetadata metadata;

    @Nonnull
    @Version
    private long concurrencyVersion;

    @Builder
    private File(@Nonnull FileId fileId,
                 @Nonnull FileDescription description,
                 @Nonnull FileMetadata metadata) {
        this.fileId = fileId;
        this.description = description;
        this.metadata = metadata;
    }

    @Nonnull
    public FileId fileId() {
        return this.fileId;
    }

    @Nonnull
    public FileDescription description() {
        return this.description;
    }

    @Nonnull
    public FileMetadata metadata() {
        return this.metadata;
    }

    @Nonnull
    public LocalDateTime createdDate() {
        return this.createdDate;
    }

    @Nonnull
    public LocalDateTime updatedDate() {
        return this.updatedDate;
    }

    void setCreatedDate(@Nonnull LocalDateTime date) {
        this.createdDate = date;
    }

    void setUpdatedDate(@Nonnull LocalDateTime date) {
        this.updatedDate = date;
    }

}
