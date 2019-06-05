package valeriy.knyazhev.architector.port.adapter.resources.project.file.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bimserver.emf.Schema;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights;
import valeriy.knyazhev.architector.domain.model.util.serialization.ArchitectorLocalDateTimeSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class FileDescriptorModel
{


    @Nonnull
    private final String fileId;

    @Nonnull
    @JsonSerialize(using = ArchitectorLocalDateTimeSerializer.class)
    private final LocalDateTime createdDate;

    @Nonnull
    @JsonSerialize(using = ArchitectorLocalDateTimeSerializer.class)
    private final LocalDateTime updatedDate;

    @Nonnull
    private final ProjectAccessRights accessRights;

    @Nonnull
    private final String isoId;

    @Nonnull
    private final String schema;

    @Nonnull
    private final MetadataModel metadata;

    @Nonnull
    private final DescriptionModel description;

    @Nonnull
    private final Long currentCommitId;

    public FileDescriptorModel(@Nonnull String fileId,
                               @Nonnull LocalDateTime createdDate,
                               @Nonnull LocalDateTime updatedDate,
                               @Nonnull ProjectAccessRights accessRights,
                               @Nonnull String isoId,
                               @Nonnull String schema,
                               @Nonnull MetadataModel metadata,
                               @Nonnull DescriptionModel description,
                               @Nonnull Long currentCommitId)
    {
        this.fileId = fileId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.accessRights = accessRights;
        this.isoId = isoId;
        this.schema = schema;
        this.metadata = metadata;
        this.description = description;
        this.currentCommitId = currentCommitId;
    }

}
