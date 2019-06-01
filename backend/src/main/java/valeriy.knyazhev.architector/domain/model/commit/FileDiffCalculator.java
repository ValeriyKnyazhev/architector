package valeriy.knyazhev.architector.domain.model.commit;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.file.FileContent;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static valeriy.knyazhev.architector.domain.model.commit.CommitItem.addItem;
import static valeriy.knyazhev.architector.domain.model.commit.CommitItem.deleteItem;

/**
 * @author Valeriy Knyazhev
 */
@Service
@RequiredArgsConstructor
public final class FileDiffCalculator
{

    @Nonnull
    public static List<CommitItem> calculateDiff(@Nullable FileContent oldFile,
                                                 @Nullable FileContent newFile)
    {
        if (oldFile == null && newFile == null)
        {
            throw new IllegalStateException("Old and new files must not be null");
        }
        if (oldFile == null)
        {
            return constructAdditionItems(newFile.items(), 0);
        }
        if (newFile == null)
        {
            return constructDeletionItems(oldFile.items(), 1);
        }
        List<String> oldItems = oldFile.items();
        List<String> newItems = newFile.items();
        Patch<String> diff = DiffUtils.diff(oldItems, newItems);
        return diff.getDeltas().stream()
            .map(FileDiffCalculator::defineChangedItems)
            .flatMap(Collection::stream)
            .collect(toList());
    }

    @Nonnull
    public static FileMetadataChanges defineMetadataChanges(@Nullable FileMetadata oldMetadata,
                                                            @Nullable FileMetadata newMetadata)
    {

        if (oldMetadata == null && newMetadata == null)
        {
            throw new IllegalStateException("Old and new file metadata must not be null");
        }
        if (oldMetadata == null)
        {
            return FileMetadataChanges.builder()
                .name(newMetadata.name())
                .timestamp(newMetadata.timestamp())
                .authors(newMetadata.authors())
                .organizations(newMetadata.organizations())
                .preprocessorVersion(newMetadata.preprocessorVersion())
                .originatingSystem(newMetadata.originatingSystem())
                .authorization(newMetadata.authorization())
                .build();
        }
        if (newMetadata == null)
        {
            return FileMetadataChanges.empty();
        }
        return FileMetadataChanges.builder()
            .name(
                defineChanges(oldMetadata.name(), newMetadata.name())
            )
            .timestamp(
                defineChanges(oldMetadata.timestamp(), newMetadata.timestamp())
            )
            .authors(
                defineChanges(oldMetadata.authors(), newMetadata.authors())
            )
            .organizations(
                defineChanges(oldMetadata.organizations(), newMetadata.organizations())
            )
            .preprocessorVersion(
                defineChanges(oldMetadata.preprocessorVersion(), newMetadata.preprocessorVersion())
            )
            .originatingSystem(
                defineChanges(oldMetadata.originatingSystem(), newMetadata.originatingSystem())
            )
            .authorization(
                defineChanges(oldMetadata.authorization(), newMetadata.authorization())
            )
            .build();
    }

    @Nonnull
    public static FileDescriptionChanges defineDescriptionChanges(@Nullable FileDescription oldDescription,
                                                                  @Nullable FileDescription newDescription)
    {

        if (oldDescription == null && newDescription == null)
        {
            throw new IllegalStateException("Old and new file description must not be null");
        }
        if (oldDescription == null)
        {
            return FileDescriptionChanges.builder()
                .descriptions(newDescription.descriptions())
                .implementationLevel(newDescription.implementationLevel())
                .build();
        }
        if (newDescription == null)
        {
            return FileDescriptionChanges.empty();
        }
        return FileDescriptionChanges.builder()
            .descriptions(
                defineChanges(oldDescription.descriptions(), newDescription.descriptions())
            )
            .implementationLevel(
                defineChanges(oldDescription.implementationLevel(), newDescription.implementationLevel())
            )
            .build();
    }

    @Nonnull
    private static List<CommitItem> defineChangedItems(Delta<String> delta)
    {
        Chunk<String> originalChunk = delta.getOriginal();
        Chunk<String> revisedChunk = delta.getRevised();
        int originPosition = originalChunk.getPosition();
        if (delta.getType() == Delta.TYPE.INSERT)
        {
            return constructAdditionItems(delta.getRevised().getLines(), originPosition);
        }
        if (delta.getType() == Delta.TYPE.DELETE)
        {
            return constructDeletionItems(originalChunk.getLines(), originPosition + 1);
        }
        if (delta.getType() == Delta.TYPE.CHANGE)
        {
            List<CommitItem> originalChanges = constructDeletionItems(originalChunk.getLines(), originPosition + 1);
            List<CommitItem> revisedChanges = constructAdditionItems(revisedChunk.getLines(), originPosition);
            return Stream.concat(originalChanges.stream(), revisedChanges.stream()).collect(toList());
        }
        throw new IllegalStateException("Unsupported delta type.");
    }

    @Nonnull
    private static List<CommitItem> constructAdditionItems(@Nonnull List<String> items, int startPosition)
    {
        return items.stream()
            .map(item -> addItem(item, startPosition))
            .collect(toList());
    }

    @Nonnull
    private static List<CommitItem> constructDeletionItems(@Nonnull List<String> items, int startPosition)
    {
        AtomicInteger curIndex = new AtomicInteger(startPosition);
        return items.stream()
            .map(item -> deleteItem(item, curIndex.getAndIncrement()))
            .collect(toList());
    }

    @Nullable
    private static String defineChanges(@Nonnull String oldValue,
                                        @Nonnull String newValue)
    {
        return oldValue.equals(newValue) ? null : newValue;
    }

    @Nullable
    private static LocalDate defineChanges(@Nonnull LocalDate oldValue,
                                           @Nonnull LocalDate newValue)
    {
        return oldValue.equals(newValue) ? null : newValue;
    }

    @Nullable
    private static List<String> defineChanges(@Nonnull List<String> oldValues,
                                              @Nonnull List<String> newValues)
    {
        return CollectionUtils.isEqualCollection(oldValues, newValues) ? null : newValues;
    }

}
