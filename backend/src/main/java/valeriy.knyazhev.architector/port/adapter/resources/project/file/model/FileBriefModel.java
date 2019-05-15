package valeriy.knyazhev.architector.port.adapter.resources.project.file.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bimserver.emf.Schema;
import valeriy.knyazhev.architector.domain.model.util.serialization.ArchitectorLocalDateTimeSerializer;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class FileBriefModel
{


    @Nonnull
    private String fileId;

    @Nonnull
    private String name;

    @Nonnull
    @JsonSerialize(using = ArchitectorLocalDateTimeSerializer.class)
    private LocalDateTime createdDate;

    @Nonnull
    @JsonSerialize(using = ArchitectorLocalDateTimeSerializer.class)
    private LocalDateTime updatedDate;

    @Nonnull
    private Schema schema;

    public FileBriefModel(@Nonnull String fileId, @Nonnull String name, @Nonnull LocalDateTime createdDate,
                          @Nonnull LocalDateTime updatedDate, @Nonnull Schema schema)
    {
        this.fileId = fileId;
        this.name = name;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.schema = schema;
    }

}
