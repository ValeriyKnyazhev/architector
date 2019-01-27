package valeriy.knyazhev.architector.port.adapter.project.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import org.bimserver.emf.Schema;

import javax.annotation.Nonnull;
import java.time.LocalDate;
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
    private Schema schema;

    @Nonnull
    private DescriptionModel description;

    @Nonnull
    private MetadataModel metadata;

    @Nonnull
    private List<FileModel> files;

    public ProjectModel(@Nonnull String projectId, @Nonnull LocalDateTime createdDate,
                        @Nonnull LocalDateTime updatedDate, @Nonnull Schema schema,
                        @Nonnull DescriptionModel description, @Nonnull MetadataModel metadata,
                        @Nonnull List<FileModel> files) {
        this.projectId = projectId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.schema = schema;
        this.description = description;
        this.metadata = metadata;
        this.files = files;
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    public static class DescriptionModel {

        @Nonnull
        private List<String> descriptions;

        @Nonnull
        private String implementationLevel;

        public DescriptionModel(@Nonnull List<String> descriptions, @Nonnull String implementationLevel) {
            this.descriptions = descriptions;
            this.implementationLevel = implementationLevel;
        }
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    public static class MetadataModel {

        @Nonnull
        private String name;

        @Nonnull
        private LocalDate timestamp;

        @Nonnull
        private List<String> authors;

        @Nonnull
        private List<String> organizations;

        @Nonnull
        private String preprocessorVersion;

        @Nonnull
        private String originatingSystem;

        @Nonnull
        private String authorisation;

        @Builder
        private MetadataModel(@Nonnull String name, @Nonnull LocalDate timestamp, @Nonnull List<String> authors,
                              @Nonnull List<String> organizations, @Nonnull String preprocessorVersion,
                              @Nonnull String originatingSystem, @Nonnull String authorisation) {
            this.name = name;
            this.timestamp = timestamp;
            this.authors = authors;
            this.organizations = organizations;
            this.preprocessorVersion = preprocessorVersion;
            this.originatingSystem = originatingSystem;
            this.authorisation = authorisation;
        }
    }

}
