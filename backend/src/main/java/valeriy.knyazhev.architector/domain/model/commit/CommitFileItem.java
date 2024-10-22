package valeriy.knyazhev.architector.domain.model.commit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommitFileItem
{

    @Nonnull
    private FileId fileId;

    @Nonnull
    private String isoId;

    @Nonnull
    private String schema;

    @Nonnull
    private FileMetadataChanges metadata;

    @Nonnull
    private FileDescriptionChanges description;

    @Nonnull
    private List<CommitItem> items;

    private CommitFileItem(@Nonnull FileId fileId,
                           @Nonnull String isoId,
                           @Nonnull String schema,
                           @Nonnull FileMetadataChanges metadata,
                           @Nonnull FileDescriptionChanges description,
                           @Nonnull List<CommitItem> items)
    {
        this.fileId = fileId;
        this.isoId = isoId;
        this.schema = schema;
        this.metadata = metadata;
        this.description = description;
        this.items = items.stream()
            .sorted(CommitItem::compareTo)
            .collect(Collectors.toList());
        if (items.isEmpty() && metadata.isEmpty() && description.isEmpty())
        {
            throw new NothingToCommitException();
        }
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
    public FileMetadataChanges metadata()
    {
        return this.metadata;
    }

    @Nonnull
    public FileDescriptionChanges description()
    {
        return this.description;
    }

    @Nonnull
    public List<CommitItem> items()
    {
        return this.items;
    }

    @Nonnull
    public static CommitFileItem of(@Nonnull FileId fileId,
                                    @Nonnull String isoId,
                                    @Nonnull String schema,
                                    @Nonnull FileMetadataChanges metadata,
                                    @Nonnull FileDescriptionChanges description,
                                    @Nonnull List<CommitItem> items)
    {
        return new CommitFileItem(fileId, isoId, schema, metadata, description, items);
    }

}
