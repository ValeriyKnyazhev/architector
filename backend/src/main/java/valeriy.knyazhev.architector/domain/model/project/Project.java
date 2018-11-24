package valeriy.knyazhev.architector.domain.model.project;

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
    private Schema schema = IFC2X3TC1;

    @AttributeOverrides({
            @AttributeOverride(name = "description",
                    column = @Column(name = "description")),
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
            @AttributeOverride(name = "author",
                    column = @Column(name = "author")),
            @AttributeOverride(name = "organization",
                    column = @Column(name = "organization")),
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
    @Version
    private long concurrencyVersion;

    public Project(@Nonnull ProjectId projectId, @Nonnull ProjectDescription description,
                   @Nonnull ProjectMetadata metadata, @Nonnull Schema schema) {
        this.projectId = projectId;
        this.description = description;
        this.metadata = metadata;
        this.schema = schema;
    }

    void setCreatedDate(@Nonnull LocalDateTime date) {
        this.createdDate = date;
    }

    void setUpdatedDate(@Nonnull LocalDateTime date) {
        this.updatedDate = date;
    }

}
