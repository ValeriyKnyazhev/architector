package valeriy.knyazhev.architector.domain.model.project.commit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.commit.projection.ProjectDataProjection;
import valeriy.knyazhev.architector.domain.model.project.commit.projection.ProjectDataProjection.FileDataProjection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static valeriy.knyazhev.architector.domain.model.project.commit.ChangeType.ADDITION;
import static valeriy.knyazhev.architector.domain.model.project.commit.ChangeType.DELETION;

/**
 * @author Valeriy Knyazhev
 */
@Service
@RequiredArgsConstructor
@Slf4j
public final class CommitCombinator {


    private static void addNextProjectChangesToProjection(@Nonnull ProjectDataProjection projection,
                                                          @Nonnull CommitDescription nextCommit) {
        nextCommit.changedFiles().forEach(file -> addNextFileChangesToProjection(projection, file));
    }

    private static void addNextFileChangesToProjection(@Nonnull ProjectDataProjection projection,
                                                       @Nonnull CommitFileItem fileChanges) {
        FileDataProjection foundFile = projection.files().stream()
            .filter(file -> fileChanges.fileId().equals(file.fileId()))
            .findFirst().orElse(null);
        if (foundFile == null) {
            if (fileChanges.items().stream().anyMatch(item -> DELETION == item.type())) {
                log.warn("Commit with new file " + fileChanges.fileId() + " has a few deletion items.");
                throw new IllegalStateException("Commit with new file " + fileChanges.fileId() +
                    " has a few deletion items.");
            }
            List<String> items = fileChanges.items().stream()
                .map(CommitItem::value)
                .collect(Collectors.toList());
            projection.addNewFile(FileDataProjection.of(fileChanges.fileId(), fileChanges.name(), items));
        } else {
            AtomicInteger index = new AtomicInteger();
            List<String> newItems = fileChanges.items().stream()
                .filter(item -> item.position() == 0)
                .map(CommitItem::value)
                .collect(Collectors.toList());
            newItems.addAll(foundFile.items().stream()
                .map(item -> defineValuesInPosition(foundFile.items().get(index.getAndIncrement()),
                    fileChanges.items().stream()
                        .filter(changedItem -> index.get() == changedItem.position())
                        .collect(Collectors.toList())))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
            newItems.addAll(fileChanges.items().stream()
                .filter(item -> index.get() < item.position())
                .map(CommitItem::value)
                .collect(Collectors.toList()));
            foundFile.updateItems(newItems);
        }
    }

    @Nonnull
    private static List<String> defineValuesInPosition(@Nonnull String curValue,
                                                       @Nonnull List<CommitItem> itemsInPosition) {
        List<String> newItems = new ArrayList<>();
        if (itemsInPosition.stream().filter(item -> DELETION == item.type()).count() > 1) {
            throw new IllegalStateException("Commit must not have a few deletion items in one position.");
        }
        CommitItem deletedItem = itemsInPosition.stream()
            .filter(item -> DELETION == item.type())
            .findFirst().orElse(null);
        if (deletedItem != null) {
            if (!curValue.equals(deletedItem.value())) {
                throw new IllegalStateException("Current item does not match with deleted item.");
            }
        } else {
            newItems.add(curValue);
        }
        newItems.addAll(itemsInPosition.stream()
            .filter(item -> ADDITION == item.type())
            .map(CommitItem::value)
            .collect(Collectors.toList()));
        return newItems;
    }

    @Nonnull
    public ProjectDataProjection combineCommits(@Nonnull List<Commit> commits) {
        ProjectDataProjection projection = ProjectDataProjection.empty();
        commits.stream()
            .sorted(Comparator.comparing(Commit::timestamp))
            .map(Commit::data)
            .forEach(commit -> addNextProjectChangesToProjection(projection, commit));
        return projection;
    }

}
