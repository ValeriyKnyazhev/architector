package valeriy.knyazhev.architector.domain.model.commit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection.FileProjection;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static valeriy.knyazhev.architector.domain.model.commit.ChangeType.DELETION;

/**
 * @author Valeriy Knyazhev
 */
@Service
@RequiredArgsConstructor
@Slf4j
public final class CommitCombinator
{

    @Nonnull
    public static Projection combineCommits(@Nonnull String projectName,
                                            @Nonnull String projectDescription,
                                            @Nonnull List<Commit> commits)
    {
        Args.notBlank(projectName, "Project name is required.");
        Args.notNull(projectDescription, "Project description is required.");
        Args.notNull(commits, "Project commits are required.");
        Projection projection = Projection.initial(projectName, projectDescription);
        commits.stream()
            .sorted(Comparator.comparing(Commit::timestamp))
            .map(Commit::data)
            .forEach(commit -> addNextChangesToProjection(projection, commit));
        return projection;
    }

    private static void addNextChangesToProjection(@Nonnull Projection projection,
                                                   @Nonnull CommitDescription nextCommit)
    {
        nextCommit.changedFiles().forEach(file -> addNextFileChangesToProjection(projection, file));
    }

    private static void addNextFileChangesToProjection(@Nonnull Projection projection,
                                                       @Nonnull CommitFileItem fileChanges)
    {
        FileProjection foundFile = projection.files().stream()
            .filter(file -> fileChanges.fileId().equals(file.fileId()))
            .findFirst().orElse(null);
        if (foundFile == null)
        {
            if (fileChanges.items().stream().anyMatch(item -> DELETION == item.type()))
            {
                log.warn("Commit with new file " + fileChanges.fileId() + " has a few deletion items.");
                throw new IllegalStateException(
                    "Commit with new file " + fileChanges.fileId() + " has a few deletion items."
                );
            }
            List<String> items = fileChanges.items().stream()
                .map(CommitItem::value)
                .collect(Collectors.toList());
            projection.addNewFile(
                FileProjection.of(
                    fileChanges.fileId(),
                    fileChanges.isoId(),
                    fileChanges.schema(),
                    combineMetadataChanges(null, fileChanges.metadata()),
                    combineDescriptionChanges(null, fileChanges.description()),
                    items
                )
            );
        } else
        {
            foundFile.update(
                combineMetadataChanges(foundFile.metadata(), fileChanges.metadata()),
                combineDescriptionChanges(foundFile.description(), fileChanges.description()),
                ContentMerger.applyChanges(foundFile.items(), fileChanges.items())
            );
        }
    }

    @Nonnull
    private static FileMetadata combineMetadataChanges(@Nullable FileMetadata metadata,
                                                       @Nonnull FileMetadataChanges changes)
    {
        if (metadata == null)
        {
            if (changes.isEmpty())
            {
                throw new IllegalStateException("All metadata fields should have been filled.");
            }
            return FileMetadata.builder()
                .name(changes.name())
                .timestamp(changes.timestamp())
                .authors(changes.authors())
                .organizations(changes.organizations())
                .preprocessorVersion(changes.preprocessorVersion())
                .originatingSystem(changes.originatingSystem())
                .authorization(changes.authorization())
                .build();
        }
        return FileMetadata.builder()
            .name(changes.newName(metadata.name()))
            .timestamp(changes.newTimestamp(metadata.timestamp()))
            .authors(changes.newAuthors(metadata.authors()))
            .organizations(changes.newOrganizations(metadata.organizations()))
            .preprocessorVersion(changes.newPreprocessorVersion(metadata.preprocessorVersion()))
            .originatingSystem(changes.newOriginatingSystem(metadata.originatingSystem()))
            .authorization(changes.newAuthorization(metadata.authorization()))
            .build();
    }

    @Nonnull
    private static FileDescription combineDescriptionChanges(@Nullable FileDescription description,
                                                             @Nonnull FileDescriptionChanges changes)
    {
        if (description == null)
        {
            if (changes.isEmpty())
            {
                throw new IllegalStateException("All description fields should have been filled.");
            }
            return FileDescription.of(changes.descriptions(), changes.implementationLevel());
        }
        return FileDescription.of(
            changes.newDescriptions(description.descriptions()),
            changes.newImplementationLevel(description.implementationLevel())
        );
    }

}
