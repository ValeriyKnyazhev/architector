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
    private FileChangesStatistics statistics;

    @Nonnull
    private FileMetadataChangesData metadata;

    @Nonnull
    private FileDescriptionChangesData description;

    @Nonnull
    private List<SectionChangesData> sections;

    public FileChangesData(@Nonnull String fileId,
                           @Nonnull FileChangesStatistics statistics,
                           @Nonnull FileMetadataChangesData metadata,
                           @Nonnull FileDescriptionChangesData description,
                           @Nonnull List<SectionChangesData> sections)
    {
        this.fileId = Args.notNull(fileId, "File identifier is required.");
        this.statistics = Args.notNull(statistics, "File changes statistics is required.");
        this.metadata = Args.notNull(metadata, "File metadata is required.");
        this.description = Args.notNull(description, "File description is required.");
        this.sections = Args.notNull(sections, "File sections are required.");
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    public static class FileChangesStatistics
    {

        @Nonnull
        private FileModificationType type;

        private long addedLines;

        private long deletedLines;

        private boolean characteristicsModified;

        private FileChangesStatistics(@Nonnull FileModificationType type,
                                      long addedLines,
                                      long deletedLines,
                                      boolean characteristicsModified)
        {
            this.type = Args.notNull(type, "File modification type is required.");
            this.addedLines = Args.notNegative(addedLines, "Added lines count must not be negative.");
            this.deletedLines = Args.notNegative(deletedLines, "Deleted lines count must not be negative.");
            this.characteristicsModified = characteristicsModified;
        }

        public static FileChangesStatistics added(long count, boolean characteristicsModified)
        {
            return new FileChangesStatistics(FileModificationType.ADDED, count, 0, characteristicsModified);
        }

        public static FileChangesStatistics deleted(long count, boolean characteristicsModified)
        {
            return new FileChangesStatistics(FileModificationType.DELETED, 0, count, characteristicsModified);
        }

        public static FileChangesStatistics modified(long addedLines,
                                                     long deletedLines,
                                                     boolean characteristicsModified)
        {
            return new FileChangesStatistics(
                FileModificationType.MODIFIED, addedLines, deletedLines, characteristicsModified
            );
        }

        public enum FileModificationType
        {

            ADDED, DELETED, MODIFIED

        }


    }

}
