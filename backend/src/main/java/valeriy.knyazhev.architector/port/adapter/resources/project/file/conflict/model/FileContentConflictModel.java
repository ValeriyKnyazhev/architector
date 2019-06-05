package valeriy.knyazhev.architector.port.adapter.resources.project.file.conflict.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictChanges;
import valeriy.knyazhev.architector.application.project.file.conflict.data.DescriptionConflictChanges;

import javax.annotation.Nonnull;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class FileContentConflictModel
{

    @Nonnull
    private List<String> oldContent;

    @Nonnull
    private List<ContentConflictChanges.ContentChangesBlock> headBlocks;

    @Nonnull
    private List<ContentConflictChanges.ContentChangesBlock> newBlocks;

    @Nonnull
    private Long headCommitId;

    @Nonnull
    private final Links links;

    public FileContentConflictModel(@Nonnull List<String> oldContent,
                                    @Nonnull List<ContentConflictChanges.ContentChangesBlock> headBlocks,
                                    @Nonnull List<ContentConflictChanges.ContentChangesBlock> newBlocks,
                                    @Nonnull Long headCommitId,
                                    @Nonnull Links links)
    {
        this.oldContent = Args.notNull(oldContent, "File content is required.");
        this.headBlocks = Args.notNull(headBlocks, "Head changes blocks are required.");
        this.newBlocks = Args.notNull(newBlocks, "New changes blocks are required.");
        this.headCommitId = Args.notNull(headCommitId, "Head commit identifier is required.");
        this.links = Args.notNull(links, "Links are required.");
    }

    @JsonAutoDetect(fieldVisibility =  ANY)
    public static class Links
    {

        @Nonnull
        private final String resolveConflict;

        private Links(@Nonnull String resolveConflict)
        {
            this.resolveConflict = Args.notNull(resolveConflict, "Resolve conflict link is required.");
        }

        public static Links of(@Nonnull String projectId,
                               @Nonnull String fileId)
        {
            Args.notBlank(projectId, "Project identifier is required.");
            Args.notBlank(fileId, "File identifier is required.");
            return new Links(
                "/api/projects/" + projectId + "/files/" + fileId + "/content/resolve-conflict"

            );
        }

    }

}
