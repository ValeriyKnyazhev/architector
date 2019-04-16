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
public class FileContentModel {


    @Nonnull
    private String fileId;

    private ContentModel content;


    public FileContentModel(@Nonnull String fileId, @Nonnull ContentModel content) {
        this.fileId = fileId;
        this.content = content;
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    public static class ContentModel {

        @Nonnull
        private List<String> items;

        public ContentModel(@Nonnull List<String> items) {
            this.items = items;
        }
    }

}
