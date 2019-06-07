package valeriy.knyazhev.architector.domain.model.project.file;

import lombok.NoArgsConstructor;
import org.apache.http.util.Args;
import org.hibernate.annotations.Type;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PROTECTED;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Entity
@EntityListeners(FileEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@Table(name = "files")
public class File
{

    private final static List<String> ALLOWED_SCHEMAS = Stream.of(IFCSchema.values())
        .map(Enum::name)
        .collect(Collectors.toList());

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fileIdGenerator")
    @SequenceGenerator(name = "fileIdGenerator", sequenceName = "file_id_seq", allocationSize = 1)
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
    private String isoId;

    @Nonnull
    private String schema;

    @Nonnull
    @Column(columnDefinition = "jsonb")
    @Type(type = "jsonb_type")
    private FileDescription description;

    @Nonnull
    @Column(columnDefinition = "jsonb")
    @Type(type = "jsonb_type")
    private FileMetadata metadata;

    @Nonnull
    @Column(columnDefinition = "jsonb")
    @Type(type = "jsonb_type")
    private FileContent content;

    @Nonnull
    @Version
    private long concurrencyVersion;

    private File(@Nonnull FileId fileId,
                 @Nonnull String isoId,
                 @Nonnull String schema,
                 @Nonnull FileDescription description,
                 @Nonnull FileMetadata metadata,
                 @Nonnull FileContent content)
    {
        this.fileId = fileId;
        this.isoId = isoId;
        Args.check(ALLOWED_SCHEMAS.contains(schema), "Schema must be equals one of " + ALLOWED_SCHEMAS.toString());
        this.schema = schema;
        this.description = description;
        this.metadata = metadata;
        this.content = content;
    }

    @Nonnull
    public FileId fileId()
    {
        return this.fileId;
    }

    @Nonnull
    public String isoId()
    {
        return this.isoId;
    }

    @Nonnull
    public String schema()
    {
        return this.schema;
    }

    @Nonnull
    public FileDescription description()
    {
        return this.description;
    }

    @Nonnull
    public FileMetadata metadata()
    {
        return this.metadata;
    }

    @Nonnull
    public FileContent content()
    {
        return this.content;
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

    public void updateDescription(@Nonnull FileDescription description)
    {
        this.description = description;
    }

    public void updateMetadata(@Nonnull FileMetadata metadata)
    {
        this.metadata = metadata;
    }

    public void updateContent(@Nonnull FileContent content)
    {
        this.content = content;
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
    public static FileConstructor constructor()
    {
        return new FileConstructor();
    }

    public static class FileConstructor
    {

        private FileId fileId;

        private String isoId;

        private String schema;

        private FileDescription description;

        private FileMetadata metadata;

        private FileContent content;

        FileConstructor()
        {
        }

        @Nonnull
        public FileConstructor withFileId(@Nonnull FileId fileId)
        {
            this.fileId = fileId;
            return this;
        }

        @Nonnull
        public FileConstructor withIsoId(@Nonnull String isoId)
        {
            this.isoId = isoId;
            return this;
        }

        @Nonnull
        public FileConstructor withSchema(@Nonnull String schema)
        {
            this.schema = schema;
            return this;
        }

        @Nonnull
        public FileConstructor withDescription(@Nonnull FileDescription description)
        {
            this.description = description;
            return this;
        }

        @Nonnull
        public FileConstructor withMetadata(@Nonnull FileMetadata metadata)
        {
            this.metadata = metadata;
            return this;
        }

        @Nonnull
        public FileConstructor withContent(@Nonnull FileContent content)
        {
            this.content = content;
            return this;
        }

        @Nonnull
        public File construct()
        {
            return new File(
                this.fileId, this.isoId, this.schema, this.description, this.metadata, this.content
            );
        }

    }

}
