package valeriy.knyazhev.architector.application.project.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valeriy.knyazhev.architector.domain.model.commit.CommitItem;
import valeriy.knyazhev.architector.domain.model.commit.CommitRepository;
import valeriy.knyazhev.architector.domain.model.commit.FileDescriptionChanges;
import valeriy.knyazhev.architector.domain.model.commit.FileMetadataChanges;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RequiredArgsConstructor
@Service
@Transactional
public class ChangesConflictApplicationService
{

    private final ProjectRepository projectRepository;

    private final IFCFileReader fileReader;

    private final CommitRepository commitRepository;

    @Nonnull
    public List<ContentConflictChanges> checkContentChangesConflicts(@Nonnull List<CommitItem> headItems,
                                                                     @Nonnull List<CommitItem> newItems)
    {
        // TODO add check conflicts
        return List.of();
    }

    @Nonnull
    public MetadataConflictChanges checkMetadataChangesConflicts(@Nonnull FileMetadata oldMetadata,
                                                                 @Nonnull FileMetadataChanges headMetadata,
                                                                 @Nonnull FileMetadataChanges newMetadata)
    {
        var conflictsBuilder = MetadataConflictChanges.builder();
        if (headMetadata.name() != null || newMetadata.name() != null)
        {
            conflictsBuilder.name(
                ConflictChange.withConflict(
                    oldMetadata.name(),
                    headMetadata.name(),
                    newMetadata.name()
                )
            );
        } else {
            conflictsBuilder.name(ConflictChange.oldValue(oldMetadata.name()));
        }
        if (headMetadata.timestamp() != null || newMetadata.timestamp() != null)
        {
            conflictsBuilder.timestamp(
                ConflictChange.withConflict(
                    oldMetadata.timestamp(),
                    headMetadata.timestamp(),
                    newMetadata.timestamp()
                )
            );
        } else {
            conflictsBuilder.timestamp(ConflictChange.oldValue(oldMetadata.timestamp()));
        }
        if (headMetadata.authors() != null || newMetadata.authors() != null)
        {
            conflictsBuilder.authors(
                ConflictChange.withConflict(
                    oldMetadata.authors(),
                    headMetadata.authors(),
                    newMetadata.authors()
                )
            );
        } else
        {
            conflictsBuilder.authors(ConflictChange.oldValue(oldMetadata.authors()));

        }
        if (headMetadata.organizations() != null || newMetadata.organizations() != null)
        {
            conflictsBuilder.organizations(
                ConflictChange.withConflict(
                    oldMetadata.organizations(),
                    headMetadata.organizations(),
                    newMetadata.organizations()
                )
            );
        } else
        {
            conflictsBuilder.organizations(ConflictChange.oldValue(oldMetadata.organizations()));
        }
        if (headMetadata.preprocessorVersion() != null || newMetadata.preprocessorVersion() != null)
        {
            conflictsBuilder.preprocessorVersion(
                ConflictChange.withConflict(
                    oldMetadata.preprocessorVersion(),
                    headMetadata.preprocessorVersion(),
                    newMetadata.preprocessorVersion()
                )
            );
        } else
        {
            conflictsBuilder.preprocessorVersion(ConflictChange.oldValue(oldMetadata.preprocessorVersion()));
        }
        if (headMetadata.originatingSystem() != null || newMetadata.originatingSystem() != null)
        {
            conflictsBuilder.originatingSystem(
                ConflictChange.withConflict(
                    oldMetadata.originatingSystem(),
                    headMetadata.originatingSystem(),
                    newMetadata.originatingSystem()
                )
            );
        } else
        {
            conflictsBuilder.originatingSystem(ConflictChange.oldValue(oldMetadata.originatingSystem()));
        }
        if (headMetadata.authorization() != null || newMetadata.authorization() != null)
        {
            conflictsBuilder.authorization(
                ConflictChange.withConflict(
                    oldMetadata.authorization(),
                    headMetadata.authorization(),
                    newMetadata.authorization()
                )
            );
        } else
        {
            conflictsBuilder.authorization(ConflictChange.oldValue(oldMetadata.authorization()));
        }
        return conflictsBuilder.build();
    }

    @Nonnull
    public DescriptionConflictChanges checkDescriptionChangesConflicts(@Nonnull FileDescription oldDescription,
                                                                       @Nonnull FileDescriptionChanges headDescription,
                                                                       @Nonnull FileDescriptionChanges newDescription)
    {
        var conflictsBuilder = DescriptionConflictChanges.builder();
        if (headDescription.descriptions() != null || newDescription.descriptions() != null)
        {
            conflictsBuilder.descriptions(
                ConflictChange.withConflict(
                    oldDescription.descriptions(),
                    headDescription.descriptions(),
                    newDescription.descriptions()
                )
            );
        } else
        {
            conflictsBuilder.descriptions(ConflictChange.oldValue(oldDescription.descriptions()));
        }
        if (headDescription.implementationLevel() != null || newDescription.implementationLevel() != null)
        {
            conflictsBuilder.implementationLevel(
                ConflictChange.withConflict(
                    oldDescription.implementationLevel(),
                    headDescription.implementationLevel(),
                    newDescription.implementationLevel()
                )
            );
        } else
        {
            conflictsBuilder.implementationLevel(ConflictChange.oldValue(oldDescription.implementationLevel()));
        }
        return conflictsBuilder.build();
    }

    public static class ContentConflictChanges
    {

    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class MetadataConflictChanges
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

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class DescriptionConflictChanges
    {

        @Nonnull
        private ConflictChange descriptions;

        @Nonnull
        private ConflictChange implementationLevel;

        @Builder
        private DescriptionConflictChanges(@Nonnull ConflictChange descriptions,
                                           @Nonnull ConflictChange implementationLevel)
        {
            this.descriptions = descriptions;
            this.implementationLevel = implementationLevel;
        }

        @JsonIgnore
        public boolean isEmpty()
        {
            return !this.descriptions.hasConflict() && !this.implementationLevel.hasConflict();
        }

        @Nonnull
        public ConflictChange descriptions()
        {
            return this.descriptions;
        }

        @Nonnull
        public ConflictChange implementationLevel()
        {
            return this.implementationLevel;
        }

    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class ConflictChange
    {

        @Nonnull
        private Object oldValue;

        @Nullable
        private Object headValue;

        @Nullable
        private Object newValue;

        private ConflictChange(@Nonnull Object oldValue,
                               @Nullable Object headValue,
                               @Nullable Object newValue)
        {
            this.oldValue = oldValue;
            this.headValue = headValue;
            this.newValue = newValue;
        }

        public boolean hasConflict()
        {
            return this.headValue != null && this.newValue != null;
        }

        @Nonnull
        public Object oldValue()
        {
            return this.oldValue;
        }

        @Nullable
        public Object headValue()
        {
            return this.headValue;
        }

        @Nullable
        public Object newValue()
        {
            return this.newValue;
        }

        public static ConflictChange withConflict(@Nonnull String oldValue,
                                                  @Nullable String headValue,
                                                  @Nullable String newValue)
        {
            return new ConflictChange(oldValue, headValue, newValue);
        }

        public static ConflictChange withConflict(@Nonnull List<String> oldValue,
                                                  @Nullable List<String> headValue,
                                                  @Nullable List<String> newValue)
        {
            return new ConflictChange(oldValue, headValue, newValue);
        }

        public static ConflictChange withConflict(@Nonnull LocalDate oldValue,
                                                  @Nullable LocalDate headValue,
                                                  @Nullable LocalDate newValue)
        {
            return new ConflictChange(oldValue, headValue, newValue);
        }

        public static ConflictChange oldValue(@Nonnull String oldValue)
        {
            return new ConflictChange(oldValue, null, null);
        }

        public static ConflictChange oldValue(@Nonnull List<String> oldValue)
        {
            return new ConflictChange(oldValue, null, null);
        }

        public static ConflictChange oldValue(@Nonnull LocalDate oldValue)
        {
            return new ConflictChange(oldValue, null, null);
        }

    }

}

