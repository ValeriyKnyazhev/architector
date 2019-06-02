package valeriy.knyazhev.architector.port.adapter.resources.project.file.conflict;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.application.project.file.conflict.DescriptionConflictChanges;

import javax.annotation.Nonnull;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class FileDescriptionConflictModel
{


    @Nonnull
    private final DescriptionConflictChanges conflictData;

    @Nonnull
    private Long headCommitId;

    @Nonnull
    private final Links links;

    public FileDescriptionConflictModel(@Nonnull DescriptionConflictChanges conflictData,
                                        @Nonnull Long headCommitId,
                                        @Nonnull Links links)
    {
        this.conflictData = Args.notNull(conflictData, "Conflict changes data is required.");
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
                "/api/projects/" + projectId + "/files/" + fileId + "/description/resolve-conflict"

            );
        }

    }

}
