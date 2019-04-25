package valeriy.knyazhev.architector.domain.model.project.commit;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.file.File;

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
            return constructDeletionItems(originalChunk.getLines(), originPosition);
        }
        if (delta.getType() == Delta.TYPE.CHANGE) {
            List<CommitItem> originalChanges = constructDeletionItems(originalChunk.getLines(), originPosition);
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
                .map(item -> deleteItem(item, curIndex.incrementAndGet()))
                .collect(toList());
    }


    @Nonnull
    public List<CommitItem> calculateDiff(@Nullable File oldFile, @Nonnull File newFile) {
        if (oldFile == null) {
            return newFile.content().items().stream()
                    .map(item -> addItem(item, 0))
                    .collect(toList());
        }
        List<String> oldItems = oldFile.content().items();
        List<String> newItems = newFile.content().items();
        Patch<String> diff = DiffUtils.diff(oldItems, newItems);
        return diff.getDeltas().stream()
                .map(FileDiffCalculator::defineChangedItems)
                .flatMap(Collection::stream)
                .collect(toList());
    }

}
