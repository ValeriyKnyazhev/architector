package valeriy.knyazhev.architector.application.project.file.conflict.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MetadataConflictChanges
{

    @Nonnull
    private ConflictChange name;

    @Nonnull
    private ConflictChange timestamp;

    @Nonnull
    private ConflictChange authors;

    @Nonnull
    private ConflictChange organizations;

    @Nonnull
    private ConflictChange preprocessorVersion;

    @Nonnull
    private ConflictChange originatingSystem;

    @Nonnull
    private ConflictChange authorization;

    @Builder
    private MetadataConflictChanges(@Nonnull ConflictChange name,
                                    @Nonnull ConflictChange timestamp,
                                    @Nonnull ConflictChange authors,
                                    @Nonnull ConflictChange organizations,
                                    @Nonnull ConflictChange preprocessorVersion,
                                    @Nonnull ConflictChange originatingSystem,
                                    @Nonnull ConflictChange authorization)
    {
        this.name = name;
        this.timestamp = timestamp;
        this.authors = authors;
        this.organizations = organizations;
        this.preprocessorVersion = preprocessorVersion;
        this.originatingSystem = originatingSystem;
        this.authorization = authorization;
    }

    @JsonIgnore
    public boolean isEmpty()
    {
        return !this.name.hasConflict() && !this.timestamp.hasConflict() && !this.authors.hasConflict() &&
               !this.organizations.hasConflict() && !this.preprocessorVersion.hasConflict() &&
               !this.originatingSystem.hasConflict() && !this.authorization.hasConflict();
    }

    @Nonnull
    public ConflictChange name()
    {
        return this.name;
    }

    @Nonnull
    public ConflictChange timestamp()
    {
        return this.timestamp;
    }

    @Nonnull
    public ConflictChange authors()
    {
        return this.authors;
    }

    @Nonnull
    public ConflictChange organizations()
    {
        return this.organizations;
    }

    @Nonnull
    public ConflictChange preprocessorVersion()
    {
        return this.preprocessorVersion;
    }

    @Nonnull
    public ConflictChange originatingSystem()
    {
        return this.originatingSystem;
    }

    @Nonnull
    public ConflictChange authorization()
    {
        return this.authorization;
    }

}