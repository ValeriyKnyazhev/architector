package valeriy.knyazhev.architector.application.commit.data.changes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.http.util.Args;

import javax.annotation.Nonnull;
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
    private List<SectionChangesData> sections;

    public FileChangesData(@Nonnull String fileId,
                           @Nonnull FileMetadataChangesData metadata,
                           @Nonnull FileDescriptionChangesData description,
                           @Nonnull List<SectionChangesData> sections)
    {
        this.fileId = Args.notNull(fileId, "File identifier is required.");
        this.metadata = Args.notNull(metadata, "File metadata is required.");
        this.description = Args.notNull(description, "File description is required.");
        this.sections = Args.notNull(sections, "File sections are required.");
    }

}
