package valeriy.knyazhev.architector.port.adapter.project.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import org.bimserver.emf.Schema;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static org.bimserver.emf.Schema.IFC2X3TC1;

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

    @Nonnull
    private Schema schema = IFC2X3TC1;

    @Nonnull
    private FileDescriptionModel description;

    @Nonnull
    private FileMetadataModel metadata;


    public FileModel(@Nonnull String fileId, @Nonnull LocalDateTime createdDate,
                     @Nonnull LocalDateTime updatedDate, @Nonnull FileDescriptionModel description,
                     @Nonnull FileMetadataModel metadata) {
        this.fileId = fileId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.description = description;
        this.metadata = metadata;
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    public static class FileDescriptionModel {

        @Nonnull
        private List<String> descriptions;

        @Nonnull
        private String implementationLevel;

        public FileDescriptionModel(@Nonnull List<String> descriptions, @Nonnull String implementationLevel) {
            this.descriptions = descriptions;
            this.implementationLevel = implementationLevel;
        }
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    public static class FileMetadataModel {

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
        private FileMetadataModel(@Nonnull String name, @Nonnull LocalDate timestamp, @Nonnull List<String> authors,
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
