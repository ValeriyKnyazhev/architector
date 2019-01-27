package valeriy.knyazhev.architector.port.adapter.project.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class FileModel {


    @Nonnull
    private String fileId;

    @Nonnull
    private LocalDateTime createdDate;

    @Nonnull
    private LocalDateTime updatedDate;

    public FileModel(@Nonnull String fileId, @Nonnull LocalDateTime createdDate,
                     @Nonnull LocalDateTime updatedDate) {
        this.fileId = fileId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

}
