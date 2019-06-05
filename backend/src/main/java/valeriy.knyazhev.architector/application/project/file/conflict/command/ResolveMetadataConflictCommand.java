package valeriy.knyazhev.architector.application.project.file.conflict.command;

import lombok.Builder;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class ResolveMetadataConflictCommand
{

    @Nonnull
    private String projectId;

    @Nonnull
    private String fileId;

    @Nonnull
    private Architector architector;

    @Nonnull
    private Long headCommitId;

    @Nonnull
    private String name;

    @Nonnull
    private LocalDateTime timestamp;

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
    private ResolveMetadataConflictCommand(@Nonnull String projectId,
                                          @Nonnull String fileId,
                                          @Nonnull Architector architector,
                                          @Nonnull Long headCommitId,
                                          @Nonnull String name,
                                          @Nonnull LocalDateTime timestamp,
                                          @Nonnull List<String> authors,
                                          @Nonnull List<String> organizations,
                                          @Nonnull String preprocessorVersion,
                                          @Nonnull String originatingSystem,
                                          @Nonnull String authorization)
    {
        this.projectId = projectId;
        this.fileId = fileId;
        this.architector = architector;
        this.name = name;
        this.timestamp = timestamp;
        this.authors = authors;
        this.organizations = organizations;
        this.preprocessorVersion = preprocessorVersion;
        this.originatingSystem = originatingSystem;
        this.authorization = authorization;
        this.headCommitId = headCommitId;
    }

    @Nonnull
    public ProjectId projectId()
    {
        return ProjectId.of(this.projectId);
    }

    @Nonnull
    public FileId fileId()
    {
        return FileId.of(this.fileId);
    }

    @Nonnull
    public Architector architector()
    {
        return this.architector;
    }

    @Nonnull
    public FileMetadata fixedMetadata()
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

    public long headCommitId()
    {
        return this.headCommitId;
    }

}
