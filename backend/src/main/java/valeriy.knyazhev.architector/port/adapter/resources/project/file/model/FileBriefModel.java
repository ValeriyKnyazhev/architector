package valeriy.knyazhev.architector.port.adapter.resources.project.file.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.bimserver.emf.Schema;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class FileBriefModel {


    @Nonnull
    private String fileId;

    @Nonnull
    private LocalDateTime createdDate;

    @Nonnull
    private LocalDateTime updatedDate;

    @Nonnull
    private Schema schema;

    public FileBriefModel(@Nonnull String fileId, @Nonnull LocalDateTime createdDate,
                          @Nonnull LocalDateTime updatedDate, @Nonnull Schema schema) {
        this.fileId = fileId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

}
