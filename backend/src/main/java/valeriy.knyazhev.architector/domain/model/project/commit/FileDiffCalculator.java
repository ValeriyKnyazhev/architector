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

    private static List<CommitItem> defineChangedItems(Delta<String> delta) {
        Chunk<String> originalChunk = delta.getOriginal();
        int position = originalChunk.getPosition() + 1;
        if (delta.getType() == Delta.TYPE.INSERT) {
            return delta.getRevised().getLines().stream()
                    .map(line -> addItem(line, position))
                    .collect(toList());
        }
        if (delta.getType() == Delta.TYPE.DELETE) {
            return originalChunk.getLines().stream()
                    .map(line -> deleteItem(line, position))
                    .collect(toList());
        }
        Chunk<String> revisedChunk = delta.getRevised();
        Stream<CommitItem> originalChanges = originalChunk.getLines().stream()
                .map(line -> deleteItem(line, position));
        Stream<CommitItem> revisedChanges = revisedChunk.getLines().stream()
                .map(line -> addItem(line, position));
        return Stream.concat(originalChanges, revisedChanges).collect(toList());
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
