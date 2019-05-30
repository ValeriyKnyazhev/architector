package valeriy.knyazhev.architector.port.adapter.resources.project.file.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.util.serialization.ArchitectorLocalDateDeserializer;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateFileMetadataRequest
{

    @Nonnull
    private String name;

    @Nonnull
    @JsonDeserialize(using = ArchitectorLocalDateDeserializer.class)
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

    @Nonnull
    private Long headCommitId;

    public void setName(@Nonnull String name)
    {
        this.name = Args.notNull(name, "Name is required.");
    }

    public void setTimestamp(@Nonnull LocalDate timestamp)
    {
        this.timestamp = Args.notNull(timestamp, "Timestamp is required.");
    }

    public void setAuthors(@Nonnull List<String> authors)
    {
        this.authors = Args.notNull(authors, "Authors are required.");
    }

    public void setOrganizations(@Nonnull List<String> organizations)
    {
        this.organizations = Args.notNull(organizations, "Organizations are required.");
    }

    public void setPreprocessorVersion(@Nonnull String preprocessorVersion)
    {
        this.preprocessorVersion = Args.notNull(preprocessorVersion, "Preprocessor version is required.");
    }

    public void setOriginatingSystem(@Nonnull String originatingSystem)
    {
        this.originatingSystem = Args.notNull(originatingSystem, "Originating system is required.");
    }

    public void setAuthorization(@Nonnull String authorization)
    {
        this.authorization = Args.notNull(authorization, "Authorization is required.");
    }

    public void setHeadCommitId(@Nonnull Long headCommitId)
    {
        this.headCommitId = Args.notNull(headCommitId, "Head commit identifier is required.");
    }

    @Nonnull
    public String name()
    {
        return this.name;
    }

    @Nonnull
    public LocalDate timestamp()
    {
        return this.timestamp;
    }

    @Nonnull
    public List<String> authors()
    {
        return this.authors;
    }

    @Nonnull
    public List<String> organizations()
    {
        return this.organizations;
    }

    @Nonnull
    public String preprocessorVersion()
    {
        return this.preprocessorVersion;
    }

    @Nonnull
    public String originatingSystem()
    {
        return this.originatingSystem;
    }

    @Nonnull
    public String authorization()
    {
        return this.authorization;
    }

    public long headCommitId()
    {
        return this.headCommitId;
    }

}
