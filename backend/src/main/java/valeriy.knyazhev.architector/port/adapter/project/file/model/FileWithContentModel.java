package valeriy.knyazhev.architector.port.adapter.project.file.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class FileWithContentModel {


    @Nonnull
    private String fileId;

    @Nonnull
    private LocalDateTime createdDate;

    @Nonnull
    private LocalDateTime updatedDate;

    private FileContentModel content;


    public FileWithContentModel(@Nonnull String fileId, @Nonnull LocalDateTime createdDate,
                                @Nonnull LocalDateTime updatedDate, @Nonnull FileContentModel content) {
        this.fileId = fileId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.content = content;
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    public static class FileContentModel {

        @Nonnull
        private List<String> items;

        public FileContentModel(@Nonnull List<String> items) {
            this.items = items;
        }
    }

}
