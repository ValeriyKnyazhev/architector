package valeriy.knyazhev.architector.domain.model.project.commit;

import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.TABLE;
import static lombok.AccessLevel.PROTECTED;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "commits")
public class Commit {


    @Id
    @GeneratedValue(strategy = TABLE)
    private long id;

    @Nullable
    private Long parentId;

    @AttributeOverride(name = "id", column = @Column(name = "project_id", nullable = false, updatable = false))
    @Embedded
    @Nonnull
    private ProjectId projectId;

    @Nonnull
    private String author;

    @Nonnull
    private String message;

    @Nonnull
    private LocalDateTime timestamp;

    @Nonnull
    @Column(columnDefinition = "jsonb")
    @Type(type = "jsonb_type")
    private CommitDescription data;

    @Builder
    private Commit(@Nullable Long parentId, @Nonnull ProjectId projectId,
                   @Nonnull String author, @Nonnull String message,
                   @Nonnull CommitDescription data) {
        this.parentId = parentId;
        this.projectId = projectId;
        this.author = author;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }

    public long id() {
        return this.id;
    }

    @Nullable
    public Long parentId() {
        return this.parentId;
    }

    @Nonnull
    public ProjectId projectId() {
        return this.projectId;
    }

    @Nonnull
    public String author() {
        return this.author;
    }

    @Nonnull
    public String message() {
        return this.message;
    }

    @Nonnull
    public LocalDateTime timestamp() {
        return this.timestamp;
    }

    @Nonnull
    public CommitDescription data() {
        return this.data;
    }

}
