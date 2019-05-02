package valeriy.knyazhev.architector.application.project.file.command;

import lombok.Builder;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateFileMetadataCommand {

    @Nonnull
    private String projectId;

    @Nonnull
    private String fileId;

    @Nonnull
    private String author;

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
    private String authorization;

    @Builder
    private UpdateFileMetadataCommand(@Nonnull String projectId, @Nonnull String fileId,
                                      @Nonnull String author, @Nonnull String name,
                                      @Nonnull LocalDate timestamp, @Nonnull List<String> authors,
                                      @Nonnull List<String> organizations, @Nonnull String preprocessorVersion,
                                      @Nonnull String originatingSystem, @Nonnull String authorization) {
        this.projectId = projectId;
        this.fileId = fileId;
        this.author = author;
        this.name = name;
        this.timestamp = timestamp;
        this.authors = authors;
        this.organizations = organizations;
        this.preprocessorVersion = preprocessorVersion;
        this.originatingSystem = originatingSystem;
        this.authorization = authorization;
    }

    @Nonnull
    public ProjectId projectId() {
        return ProjectId.of(this.projectId);
    }

    @Nonnull
    public FileId fileId() {
        return FileId.of(this.fileId);
    }

    @Nonnull
    public String author() {
        return this.author;
    }

    @Nonnull
    public FileMetadata constructMetadata() {
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
