package valeriy.knyazhev.architector.application;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.file.File;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static valeriy.knyazhev.architector.application.FileDiffService.ChangedItem.ChangeType.ADDITION;
import static valeriy.knyazhev.architector.application.FileDiffService.ChangedItem.ChangeType.DELETION;

/**
 * @author Valeriy Knyazhev
 */
@Service
@RequiredArgsConstructor
public final class FileDiffService {

    private static List<ChangedItem> defineChangedItems(Delta<String> delta) {
        Chunk<String> originalChunk = delta.getOriginal();
        int position = originalChunk.getPosition() + 1;
        if (delta.getType() == Delta.TYPE.INSERT) {
            return delta.getRevised().getLines().stream()
                    .map(line -> new ChangedItem(line, ADDITION, position))
                    .collect(toList());
        }
        if (delta.getType() == Delta.TYPE.DELETE) {
            return originalChunk.getLines().stream()
                    .map(line -> new ChangedItem(line, DELETION, position))
                    .collect(toList());
        }
        Chunk<String> revisedChunk = delta.getRevised();
        Stream<ChangedItem> originalChanges = originalChunk.getLines().stream()
                .map(line -> new ChangedItem(line, DELETION, position));
        Stream<ChangedItem> revisedChanges = revisedChunk.getLines().stream()
                .map(line -> new ChangedItem(line, ADDITION, position));
        return Stream.concat(originalChanges, revisedChanges).collect(toList());
    }

    @Nonnull
    public List<ChangedItem> calculateDiff(@Nonnull File oldFile, @Nonnull File newFile) {
        List<String> oldItems = oldFile.content().items();
        List<String> newItems = newFile.content().items();
        Patch<String> diff = DiffUtils.diff(oldItems, newItems);
        return diff.getDeltas().stream()
                .map(FileDiffService::defineChangedItems)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    public static class ChangedItem {

        private String value;

        private ChangeType type;

        private int position;

        public ChangedItem(String value, ChangeType type, int position) {
            this.value = value;
            this.type = type;
            this.position = position;
        }

        public String value() {
            return this.value;
        }

        public ChangeType type() {
            return this.type;
        }

        public int position() {
            return this.position;
        }

        public static enum ChangeType {
            ADDITION, DELETION // ADDITION order more significant, than DELETION order
        }
    }

}
