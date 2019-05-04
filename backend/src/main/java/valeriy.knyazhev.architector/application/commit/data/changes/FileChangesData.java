package valeriy.knyazhev.architector.application.commit.data.changes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.commit.ChangeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class FileChangesData
{

    @Nonnull
    private String fileId;

    @Nonnull
    private FileMetadataChangesData metadata;

    @Nonnull
    private FileDescriptionChangesData description;

    @Nonnull
    private List<FileSection> sections;

    public FileChangesData(@Nonnull String fileId,
                           @Nonnull FileMetadataChangesData metadata,
                           @Nonnull FileDescriptionChangesData description,
                           @Nonnull List<FileSection> sections)
    {
        this.fileId = Args.notNull(fileId, "File identifier is required.");
        this.metadata = Args.notNull(metadata, "File metadata is required.");
        this.description = Args.notNull(description, "File description is required.");
        this.sections = Args.notNull(sections, "File sections are required.");
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    public static class FileSection
    {
        @Nonnull
        private List<SectionItem> items;

        public FileSection(@Nonnull List<SectionItem> items)
        {
            this.items = Args.notNull(items, "Section items are required.");
        }
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    public static class SectionItem
    {

        private int position;

        @Nonnull
        private String value;

        @Nullable
        private ChangeType type;

        public SectionItem(int position,
                           @Nonnull String value,
                           @Nullable ChangeType type)
        {
            this.position = position;
            this.value = Args.notNull(value, "Section item value is required.");
            this.type = type;
        }

    }


}
