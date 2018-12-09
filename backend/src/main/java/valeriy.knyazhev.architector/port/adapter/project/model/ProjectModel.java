package valeriy.knyazhev.architector.port.adapter.project.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class ProjectModel {


    @Nonnull
    private String projectId;

    @Nonnull
    private LocalDateTime createdDate;

    @Nonnull
    private LocalDateTime updatedDate;

    @Nonnull
    private List<FileModel> files;

    public ProjectModel(@Nonnull String projectId, @Nonnull LocalDateTime createdDate,
                        @Nonnull LocalDateTime updatedDate, @Nonnull List<FileModel> files) {
        this.projectId = projectId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.files = files;
    }

}
