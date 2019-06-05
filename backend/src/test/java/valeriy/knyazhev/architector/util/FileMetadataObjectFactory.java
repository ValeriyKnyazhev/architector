package valeriy.knyazhev.architector.util;

import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author Valeriy Knyazhev
 */
public class FileMetadataObjectFactory
{

    public static FileMetadataBuilder metadataBuilder()
    {
        return new FileMetadataBuilder();
    }

    public static final class FileMetadataBuilder
    {
        private String name = "Big Ben";

        private LocalDateTime timestamp = LocalDateTime.of(1859, 5, 31, 12, 0);

        private List<String> authors = Collections.singletonList("Augustus Pugin");

        private List<String> organizations = Collections.singletonList("Palace of Westminster");

        private String preprocessorVersion = "GeometryGymIFC v0.0.15.0";

        private String originatingSystem = "IFCBuildings";

        private String authorization = "None";

        public FileMetadataBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public FileMetadataBuilder withTimestamp(LocalDateTime timestamp)
        {
            this.timestamp = timestamp;
            return this;
        }

        public FileMetadataBuilder withAuthors(List<String> authors)
        {
            this.authors = authors;
            return this;
        }

        public FileMetadataBuilder withOrganizations(List<String> organizations)
        {
            this.organizations = organizations;
            return this;
        }

        public FileMetadataBuilder withPreprocessorVersion(String preprocessorVersion)
        {
            this.preprocessorVersion = preprocessorVersion;
            return this;
        }

        public FileMetadataBuilder withOriginatingSystem(String originatingSystem)
        {
            this.originatingSystem = originatingSystem;
            return this;
        }

        public FileMetadataBuilder withAuthorization(String authorization)
        {
            this.authorization = authorization;
            return this;
        }

        public FileMetadata build()
        {
            return FileMetadata.builder()
                .name(this.name)
                .timestamp(this.timestamp)
                .authors(this.authors)
                .organizations(this.organizations)
                .preprocessorVersion(this.preprocessorVersion)
                .originatingSystem(this.originatingSystem)
                .authorization(this.authorization)
                .build();
        }
    }
}
