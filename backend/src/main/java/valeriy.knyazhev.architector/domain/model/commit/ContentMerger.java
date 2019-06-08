package valeriy.knyazhev.architector.domain.model.commit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static valeriy.knyazhev.architector.domain.model.commit.ChangeType.ADDITION;
import static valeriy.knyazhev.architector.domain.model.commit.ChangeType.DELETION;

/**
 * @author Valeriy Knyazhev
 */
@Service
@RequiredArgsConstructor
@Slf4j
public final class ContentMerger
{

    @Nonnull
    public static List<String> applyChanges(@Nonnull List<String> content,
                                             @Nonnull List<CommitItem> changes)
    {

        AtomicInteger index = new AtomicInteger();
        List<String> newItems = changes.stream()
            .filter(item -> item.position() == 0)
            .map(CommitItem::value)
            .collect(Collectors.toList());
        newItems.addAll(
            content.stream()
                .map(item -> defineValuesInPosition(
                    content.get(index.getAndIncrement()),
                    changes.stream()
                        .filter(changedItem -> index.get() == changedItem.position())
                        .collect(Collectors.toList())
                    )
                )
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        newItems.addAll(changes.stream()
            .filter(item -> index.get() < item.position())
            .map(CommitItem::value)
            .collect(Collectors.toList()));
        return newItems;
    }

    @Nonnull
    private static List<String> defineValuesInPosition(@Nonnull String curValue,
                                                       @Nonnull List<CommitItem> itemsInPosition)
    {
        List<String> newItems = new ArrayList<>();
        if (itemsInPosition.stream().filter(item -> DELETION == item.type()).count() > 1)
        {
            throw new IllegalStateException("Commit must not have a few deletion items in one position.");
        }
        CommitItem deletedItem = itemsInPosition.stream()
            .filter(item -> DELETION == item.type())
            .findFirst().orElse(null);
        if (deletedItem != null)
        {
            if (!curValue.equals(deletedItem.value()))
            {
                throw new IllegalStateException("Current item does not match with deleted item.");
            }
        } else
        {
            newItems.add(curValue);
        }
        newItems.addAll(itemsInPosition.stream()
            .filter(item -> ADDITION == item.type())
            .map(CommitItem::value)
            .collect(Collectors.toList()));
        return newItems;
    }

}
