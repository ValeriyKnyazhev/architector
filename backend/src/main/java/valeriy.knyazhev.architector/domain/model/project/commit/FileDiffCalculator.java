package valeriy.knyazhev.architector.domain.model.project.commit;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.file.FileContent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static valeriy.knyazhev.architector.domain.model.project.commit.CommitItem.addItem;
import static valeriy.knyazhev.architector.domain.model.project.commit.CommitItem.deleteItem;

/**
 * @author Valeriy Knyazhev
 */
@Service
@RequiredArgsConstructor
public final class FileDiffCalculator {

    @Nonnull
    private static List<CommitItem> defineChangedItems(Delta<String> delta) {
        Chunk<String> originalChunk = delta.getOriginal();
        Chunk<String> revisedChunk = delta.getRevised();
        int originPosition = originalChunk.getPosition();
        if (delta.getType() == Delta.TYPE.INSERT) {
            return constructAdditionItems(delta.getRevised().getLines(), originPosition);
        }
        if (delta.getType() == Delta.TYPE.DELETE) {
            return constructDeletionItems(originalChunk.getLines(), originPosition + 1);
        }
        if (delta.getType() == Delta.TYPE.CHANGE) {
            List<CommitItem> originalChanges = constructDeletionItems(originalChunk.getLines(), originPosition + 1);
            List<CommitItem> revisedChanges = constructAdditionItems(revisedChunk.getLines(), originPosition);
            return Stream.concat(originalChanges.stream(), revisedChanges.stream()).collect(toList());
        }
        throw new IllegalStateException("Unsupported delta type.");
    }

    @Nonnull
    private static List<CommitItem> constructAdditionItems(@Nonnull List<String> items, int startPosition) {
        return items.stream()
                .map(item -> addItem(item, startPosition))
                .collect(toList());
    }

    @Nonnull
    private static List<CommitItem> constructDeletionItems(@Nonnull List<String> items, int startPosition) {
        AtomicInteger curIndex = new AtomicInteger(startPosition);
        return items.stream()
            .map(item -> deleteItem(item, curIndex.getAndIncrement()))
                .collect(toList());
    }


    @Nonnull
    public List<CommitItem> calculateDiff(@Nullable FileContent oldFile, @Nullable FileContent newFile) {
        if (oldFile == null && newFile == null) {
            throw new IllegalStateException("Old and new files must not be null");
        }
        if (oldFile == null) {
            return constructAdditionItems(newFile.items(), 0);
        }
        if (newFile == null) {
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

}
