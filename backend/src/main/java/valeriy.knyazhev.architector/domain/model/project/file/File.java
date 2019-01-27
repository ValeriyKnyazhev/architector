package valeriy.knyazhev.architector.domain.model.project.file;

import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.TABLE;
import static lombok.AccessLevel.PROTECTED;

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
    @Column(columnDefinition = "jsonb")
    @Type(type = "file_content_jsonb")
    private FileContent content;

    @Nonnull
    @Version
    private long concurrencyVersion;

    @Builder
    private File(@Nonnull FileId fileId,
                 @Nonnull FileContent content) {
        this.fileId = fileId;
        this.content = content;
    }

    @Nonnull
    public FileId fileId() {
        return this.fileId;
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

    void setCreatedDate(@Nonnull LocalDateTime date) {
        this.createdDate = date;
    }

    void setUpdatedDate(@Nonnull LocalDateTime date) {
        this.updatedDate = date;
    }

}
