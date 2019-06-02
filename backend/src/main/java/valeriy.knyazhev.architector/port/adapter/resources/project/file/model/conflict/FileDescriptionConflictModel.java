package valeriy.knyazhev.architector.port.adapter.resources.project.file.model.conflict;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.application.project.file.ChangesConflictApplicationService;
import valeriy.knyazhev.architector.application.project.file.ChangesConflictApplicationService.DescriptionConflictChanges;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights;
import valeriy.knyazhev.architector.domain.model.util.serialization.ArchitectorLocalDateTimeSerializer;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.DescriptionModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.MetadataModel;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

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
    private final Links links;

    public FileDescriptionConflictModel(@Nonnull DescriptionConflictChanges conflictData,
                                        @Nonnull Links links)
    {
        this.conflictData = Args.notNull(conflictData, "Conflict changes data is required.");
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
