package valeriy.knyazhev.architector.application.project.file;

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
                ConflictChange.of(
                    oldMetadata.name(),
                    headMetadata.name(),
                    newMetadata.name()
                )
            );
        }
        if (headMetadata.timestamp() != null || newMetadata.timestamp() != null)
        {
            conflictsBuilder.timestamp(
                ConflictChange.of(
                    oldMetadata.timestamp(),
                    headMetadata.timestamp(),
                    newMetadata.timestamp()
                )
            );
        }
        if (headMetadata.authors() != null || newMetadata.authors() != null)
        {
            conflictsBuilder.authors(
                ConflictChange.of(
                    oldMetadata.authors(),
                    headMetadata.authors(),
                    newMetadata.authors()
                )
            );
        }
        if (headMetadata.organizations() != null || newMetadata.organizations() != null)
        {
            conflictsBuilder.organizations(
                ConflictChange.of(
                    oldMetadata.organizations(),
                    headMetadata.organizations(),
                    newMetadata.organizations()
                )
            );
        }
        if (headMetadata.preprocessorVersion() != null || newMetadata.preprocessorVersion() != null)
        {
            conflictsBuilder.preprocessorVersion(
                ConflictChange.of(
                    oldMetadata.preprocessorVersion(),
                    headMetadata.preprocessorVersion(),
                    newMetadata.preprocessorVersion()
                )
            );
        }
        if (headMetadata.originatingSystem() != null || newMetadata.originatingSystem() != null)
        {
            conflictsBuilder.originatingSystem(
                ConflictChange.of(
                    oldMetadata.originatingSystem(),
                    headMetadata.originatingSystem(),
                    newMetadata.originatingSystem()
                )
            );
        }
        if (headMetadata.authorization() != null || newMetadata.authorization() != null)
        {
            conflictsBuilder.authorization(
                ConflictChange.of(
                    oldMetadata.authorization(),
                    headMetadata.authorization(),
                    newMetadata.authorization()
                )
            );
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
                ConflictChange.of(
                    oldDescription.descriptions(),
                    headDescription.descriptions(),
                    newDescription.descriptions()
                )
            );
        }
        if (headDescription.implementationLevel() != null || newDescription.implementationLevel() != null)
        {
            conflictsBuilder.implementationLevel(
                ConflictChange.of(
                    oldDescription.implementationLevel(),
                    headDescription.implementationLevel(),
                    newDescription.implementationLevel()
                )
            );
        }
        return conflictsBuilder.build();
    }

    public static class ContentConflictChanges
    {

    }

    public static class MetadataConflictChanges
    {

        @Nullable
        private ConflictChange name;

        @Nullable
        private ConflictChange timestamp;

        @Nullable
        private ConflictChange authors;

        @Nullable
        private ConflictChange organizations;

        @Nullable
        private ConflictChange preprocessorVersion;

        @Nullable
        private ConflictChange originatingSystem;

        @Nullable
        private ConflictChange authorization;

        @Builder
        private MetadataConflictChanges(@Nullable ConflictChange name,
                                        @Nullable ConflictChange timestamp,
                                        @Nullable ConflictChange authors,
                                        @Nullable ConflictChange organizations,
                                        @Nullable ConflictChange preprocessorVersion,
                                        @Nullable ConflictChange originatingSystem,
                                        @Nullable ConflictChange authorization)
        {
            this.name = name;
            this.timestamp = timestamp;
            this.authors = authors;
            this.organizations = organizations;
            this.preprocessorVersion = preprocessorVersion;
            this.originatingSystem = originatingSystem;
            this.authorization = authorization;
        }

        public boolean isEmpty()
        {
            return this.name == null && this.timestamp == null && this.authors == null &&
                   this.organizations == null && this.preprocessorVersion == null &&
                   this.originatingSystem == null && this.authorization == null;
        }

    }

    public static class DescriptionConflictChanges
    {

        @Nullable
        private ConflictChange descriptions;

        @Nullable
        private ConflictChange implementationLevel;

        @Builder
        private DescriptionConflictChanges(@Nullable ConflictChange descriptions,
                                           @Nullable ConflictChange implementationLevel)
        {
            this.descriptions = descriptions;
            this.implementationLevel = implementationLevel;
        }

        public boolean isEmpty()
        {
            return this.descriptions == null && this.implementationLevel == null;
        }

    }

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

        public static ConflictChange of(@Nonnull String oldValue,
                                        @Nullable String headValue,
                                        @Nullable String newValue)
        {
            return new ConflictChange(oldValue, headValue, newValue);
        }

        public static ConflictChange of(@Nonnull List<String> oldValue,
                                        @Nullable List<String> headValue,
                                        @Nullable List<String> newValue)
        {
            return new ConflictChange(oldValue, headValue, newValue);
        }

        public static ConflictChange of(@Nonnull LocalDate oldValue,
                                        @Nullable LocalDate headValue,
                                        @Nullable LocalDate newValue)
        {
            return new ConflictChange(oldValue, headValue, newValue);
        }

    }

}

