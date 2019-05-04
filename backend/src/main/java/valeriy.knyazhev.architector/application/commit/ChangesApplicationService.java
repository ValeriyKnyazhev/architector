package valeriy.knyazhev.architector.application.commit;

import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valeriy.knyazhev.architector.application.commit.data.changes.*;
import valeriy.knyazhev.architector.domain.model.commit.*;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection.FileProjection;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static valeriy.knyazhev.architector.application.commit.data.changes.ChangedValue.changeValue;
import static valeriy.knyazhev.architector.application.commit.data.changes.ChangedValue.newValue;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Service
@Transactional
public class ChangesApplicationService
{

    private final CommitRepository commitRepository;

    private final ProjectionConstructService projectionConstructService;

    public ChangesApplicationService(@Nonnull CommitRepository commitRepository,
                                     @Nonnull ProjectionConstructService projectionConstructService)
    {
        this.commitRepository = Args.notNull(commitRepository, "Commit repository is required.");
        this.projectionConstructService = Args.notNull(projectionConstructService,
            "Projection construct service is required.");
    }

    @Nonnull
    public CommitChangesData fetchCommitChanges(long commitId)
    {
        Commit commitEntity = this.commitRepository.findById(commitId)
            .orElseThrow(() -> new CommitNotFoundException(commitId));
        CommitDescription commit = commitEntity.data();
        Long commitParentId = commitEntity.parentId();
        if (commitParentId == null)
        {
            // it is init commit (project creating)
            // FIXME probably will be added file changes in init commit
            return new CommitChangesData(
                newValue(commit.name()),
                newValue(commit.description()),
                Collections.emptyList()
            );
        }
        Projection projection = this.projectionConstructService.makeProjection(
            commitEntity.projectId(), commitId
        );
        String newName = commit.name();
        String newDescription = commit.description();
        return new CommitChangesData(
            newName != null ? changeValue(projection.name(), newName) : null,
            newDescription != null ? changeValue(projection.description(), newDescription) : null,
            commit.changedFiles().stream()
                .map(file -> constructFileChanges(projection, file))
                .collect(Collectors.toList())
        );
    }

    @Nonnull
    private FileChangesData constructFileChanges(@Nonnull Projection projection,
                                                 @Nonnull CommitFileItem changes)
    {
        FileProjection fileProjection = projection.files().stream()
            .filter(file -> changes.fileId().equals(file.fileId()))
            .findFirst()
            .orElse(null);
        if (fileProjection == null)
        {
            return new FileChangesData(
                changes.fileId().id(),
                constructFileMetadataChanges(null, changes.metadata()),
                constructFileDescriptionChanges(null, changes.description()),
                defineSections(Collections.emptyList(), changes.items())
            );
        }
        FileMetadataChangesData metadataChanges = constructFileMetadataChanges(
            fileProjection.metadata(), changes.metadata()
        );
        FileDescriptionChangesData descriptionChanges = constructFileDescriptionChanges(
            fileProjection.description(), changes.description()
        );
        return new FileChangesData(
            changes.fileId().id(),
            metadataChanges,
            descriptionChanges,
            defineSections(fileProjection.items(), changes.items())
        );
    }

    @Nonnull
    // TODO improve this code
    private FileMetadataChangesData constructFileMetadataChanges(
        @Nullable FileMetadata metadata,
        @Nonnull FileMetadataChanges changes)
    {
        if (metadata == null)
        {
            return FileMetadataChangesData.builder()
                .name(newValue(changes.name()))
                .timestamp(newValue(changes.timestamp()))
                .authors(newValue(changes.authors()))
                .organizations(newValue(changes.organizations()))
                .preprocessorVersion(newValue(changes.preprocessorVersion()))
                .originatingSystem(newValue(changes.originatingSystem()))
                .authorization(newValue(changes.authorization()))
                .build();
        }
        return FileMetadataChangesData.builder()
            .name(
                changes.name() != null
                    ? changeValue(metadata.name(), changes.name())
                    : null
            )
            .timestamp(
                changes.timestamp() != null
                    ? changeValue(metadata.timestamp(), changes.timestamp())
                    : null
            )
            .authors(
                changes.authors() != null
                    ? changeValue(metadata.authors(), changes.authors())
                    : null
            )
            .organizations(
                changes.organizations() != null
                    ? changeValue(metadata.organizations(), changes.organizations())
                    : null
            )
            .preprocessorVersion(
                changes.preprocessorVersion() != null
                    ? changeValue(metadata.preprocessorVersion(), changes.preprocessorVersion())
                    : null
            )
            .originatingSystem(
                changes.originatingSystem() != null
                    ? changeValue(metadata.originatingSystem(), changes
                    .originatingSystem()) : null
            )
            .authorization(
                changes.authorization() != null
                    ? changeValue(metadata.authorization(), changes.authorization())
                    : null
            )
            .build();
    }

    @Nonnull
    // TODO improve this code
    private FileDescriptionChangesData constructFileDescriptionChanges(
        @Nullable FileDescription description,
        @Nonnull FileDescriptionChanges changes)
    {
        if (description == null)
        {
            return FileDescriptionChangesData.builder()
                .descriptions(newValue(changes.descriptions()))
                .descriptions(newValue(changes.descriptions()))
                .build();
        }
        return FileDescriptionChangesData.builder()
            .descriptions(
                changes.descriptions() != null
                    ? changeValue(description.descriptions(), changes.descriptions())
                    : null
            )
            .implementationLevel(
                changes.implementationLevel() != null
                    ? changeValue(description.implementationLevel(), changes.implementationLevel())
                    : null
            )
            .build();
    }

    @Nonnull
    private List<SectionChangesData> defineSections(@Nonnull List<String> items,
                                                    @Nonnull List<CommitItem> changes)
    {
        return SectionChangesExtractor.sectionsOf(items)
            .applyChanges(changes)
            .extract();
    }

}

