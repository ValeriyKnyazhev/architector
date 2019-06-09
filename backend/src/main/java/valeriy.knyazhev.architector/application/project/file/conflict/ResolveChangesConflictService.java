package valeriy.knyazhev.architector.application.project.file.conflict;

import lombok.RequiredArgsConstructor;
import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.application.project.file.conflict.command.ResolveContentConflictCommand;
import valeriy.knyazhev.architector.application.project.file.conflict.command.ResolveDescriptionConflictCommand;
import valeriy.knyazhev.architector.application.project.file.conflict.command.ResolveMetadataConflictCommand;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ConflictChange;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictChanges;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictChanges.ContentChangesBlock;
import valeriy.knyazhev.architector.application.project.file.conflict.data.DescriptionConflictChanges;
import valeriy.knyazhev.architector.application.project.file.conflict.data.DescriptionConflictChanges.DescriptionConflictChangesBuilder;
import valeriy.knyazhev.architector.application.project.file.conflict.data.MetadataConflictChanges;
import valeriy.knyazhev.architector.application.project.file.conflict.data.MetadataConflictChanges.MetadataConflictChangesBuilder;
import valeriy.knyazhev.architector.domain.model.AccessRightsNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.*;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.*;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RequiredArgsConstructor
@Service
@Transactional
public class ResolveChangesConflictService
{

    private final ProjectRepository projectRepository;

    private final CommitRepository commitRepository;

    @Nonnull
    public ContentConflictChanges checkContentChangesConflicts(@Nonnull List<String> oldItems,
                                                               @Nonnull List<CommitItem> headItems,
                                                               @Nonnull List<CommitItem> newItems)
    {
        if (headItems.isEmpty() || newItems.isEmpty())
        {
            return ContentConflictChanges.empty();
        }
        List<ContentChangesBlock> headChangesBlocks = defineChangesBlocks(headItems);
        List<ContentChangesBlock> newChangesBlocks = defineChangesBlocks(newItems);
        return areIntersected(headChangesBlocks, newChangesBlocks)
               ? ContentConflictChanges.of(headChangesBlocks, newChangesBlocks)
               : ContentConflictChanges.empty();
    }

    public boolean resolveContentChangesConflict(@Nonnull ResolveContentConflictCommand command)
    {
        Args.notNull(command, "Resolve description conflict command is required.");
        ProjectId projectId = command.projectId();
        Architector architector = command.architector();
        FileId fileId = command.fileId();
        Project project = findProject(projectId, architector);
        File foundFile = findFile(project, fileId);
        Long projectCommitId = project.currentCommitId();
        if (projectCommitId == null)
        {
            throw new IllegalStateException("Project must have some changes.");
        }
        // TODO add check projectCommitId and command head commit id
        FileContent fixedContent = FileContent.of(command.contentItems());
        List<CommitItem> newChanges = FileDiffCalculator.calculateDiff(
            foundFile.content(), fixedContent
        );
        if (!newChanges.isEmpty())
        {
            CommitDescription commitData = CommitDescription.builder()
                .files(
                    singletonList(
                        CommitFileItem.of(
                            fileId,
                            foundFile.isoId(),
                            foundFile.schema(),
                            FileMetadataChanges.empty(),
                            FileDescriptionChanges.empty(),
                            newChanges
                        )
                    )
                )
                .build();
            Long commitId = commitChanges(
                project.projectId(),
                projectCommitId,
                architector.email(),
                "File " + fileId.id() + " content conflict was resolved.",
                commitData
            );
            foundFile.updateContent(fixedContent);
            project.updateCurrentCommitId(commitId);
            this.projectRepository.saveAndFlush(project);
        }
        return true;
    }

    @Nonnull
    public MetadataConflictChanges checkMetadataChangesConflicts(@Nonnull FileMetadata oldMetadata,
                                                                 @Nonnull FileMetadataChanges headMetadata,
                                                                 @Nonnull FileMetadataChanges newMetadata)
    {
        MetadataConflictChangesBuilder conflictsBuilder = MetadataConflictChanges.builder();
        if (headMetadata.name() != null || newMetadata.name() != null)
        {
            conflictsBuilder.name(
                ConflictChange.withConflict(
                    oldMetadata.name(),
                    headMetadata.name(),
                    newMetadata.name()
                )
            );
        } else
        {
            conflictsBuilder.name(ConflictChange.oldValue(oldMetadata.name()));
        }
        if (headMetadata.timestamp() != null || newMetadata.timestamp() != null)
        {
            conflictsBuilder.timestamp(
                ConflictChange.withConflict(
                    oldMetadata.timestamp(),
                    headMetadata.timestamp(),
                    newMetadata.timestamp()
                )
            );
        } else
        {
            conflictsBuilder.timestamp(ConflictChange.oldValue(oldMetadata.timestamp()));
        }
        if (headMetadata.authors() != null || newMetadata.authors() != null)
        {
            conflictsBuilder.authors(
                ConflictChange.withConflict(
                    oldMetadata.authors(),
                    headMetadata.authors(),
                    newMetadata.authors()
                )
            );
        } else
        {
            conflictsBuilder.authors(ConflictChange.oldValue(oldMetadata.authors()));

        }
        if (headMetadata.organizations() != null || newMetadata.organizations() != null)
        {
            conflictsBuilder.organizations(
                ConflictChange.withConflict(
                    oldMetadata.organizations(),
                    headMetadata.organizations(),
                    newMetadata.organizations()
                )
            );
        } else
        {
            conflictsBuilder.organizations(ConflictChange.oldValue(oldMetadata.organizations()));
        }
        if (headMetadata.preprocessorVersion() != null || newMetadata.preprocessorVersion() != null)
        {
            conflictsBuilder.preprocessorVersion(
                ConflictChange.withConflict(
                    oldMetadata.preprocessorVersion(),
                    headMetadata.preprocessorVersion(),
                    newMetadata.preprocessorVersion()
                )
            );
        } else
        {
            conflictsBuilder.preprocessorVersion(ConflictChange.oldValue(oldMetadata.preprocessorVersion()));
        }
        if (headMetadata.originatingSystem() != null || newMetadata.originatingSystem() != null)
        {
            conflictsBuilder.originatingSystem(
                ConflictChange.withConflict(
                    oldMetadata.originatingSystem(),
                    headMetadata.originatingSystem(),
                    newMetadata.originatingSystem()
                )
            );
        } else
        {
            conflictsBuilder.originatingSystem(ConflictChange.oldValue(oldMetadata.originatingSystem()));
        }
        if (headMetadata.authorization() != null || newMetadata.authorization() != null)
        {
            conflictsBuilder.authorization(
                ConflictChange.withConflict(
                    oldMetadata.authorization(),
                    headMetadata.authorization(),
                    newMetadata.authorization()
                )
            );
        } else
        {
            conflictsBuilder.authorization(ConflictChange.oldValue(oldMetadata.authorization()));
        }
        return conflictsBuilder.build();
    }

    public boolean resolveMetadataChangesConflict(@Nonnull ResolveMetadataConflictCommand command)
    {
        Args.notNull(command, "Resolve metadata conflict command is required.");
        ProjectId projectId = command.projectId();
        Architector architector = command.architector();
        FileId fileId = command.fileId();
        Project project = findProject(projectId, architector);
        File foundFile = findFile(project, fileId);
        Long projectCommitId = project.currentCommitId();
        if (projectCommitId == null)
        {
            throw new IllegalStateException("Project must have some changes.");
        }
        // TODO add check projectCommitId and command head commit id
        FileMetadata fixedMetadata = command.fixedMetadata();
        FileMetadataChanges newChanges = FileDiffCalculator.defineMetadataChanges(
            foundFile.metadata(), fixedMetadata
        );
        if (!newChanges.isEmpty())
        {
            CommitDescription commitData = CommitDescription.builder()
                .files(
                    singletonList(
                        CommitFileItem.of(
                            fileId,
                            foundFile.isoId(),
                            foundFile.schema(),
                            newChanges,
                            FileDescriptionChanges.empty(),
                            List.of()
                        )
                    )
                )
                .build();
            Long commitId = commitChanges(
                project.projectId(),
                projectCommitId,
                architector.email(),
                "File " + fileId.id() + " metadata conflict was resolved.",
                commitData
            );
            foundFile.updateMetadata(fixedMetadata);
            project.updateCurrentCommitId(commitId);
            this.projectRepository.saveAndFlush(project);
        }
        return true;
    }

    @Nonnull
    public DescriptionConflictChanges checkDescriptionChangesConflicts(@Nonnull FileDescription oldDescription,
                                                                       @Nonnull FileDescriptionChanges headDescription,
                                                                       @Nonnull FileDescriptionChanges newDescription)
    {
        DescriptionConflictChangesBuilder conflictsBuilder = DescriptionConflictChanges.builder();
        if (headDescription.descriptions() != null || newDescription.descriptions() != null)
        {
            conflictsBuilder.descriptions(
                ConflictChange.withConflict(
                    oldDescription.descriptions(),
                    headDescription.descriptions(),
                    newDescription.descriptions()
                )
            );
        } else
        {
            conflictsBuilder.descriptions(ConflictChange.oldValue(oldDescription.descriptions()));
        }
        if (headDescription.implementationLevel() != null || newDescription.implementationLevel() != null)
        {
            conflictsBuilder.implementationLevel(
                ConflictChange.withConflict(
                    oldDescription.implementationLevel(),
                    headDescription.implementationLevel(),
                    newDescription.implementationLevel()
                )
            );
        } else
        {
            conflictsBuilder.implementationLevel(ConflictChange.oldValue(oldDescription.implementationLevel()));
        }
        return conflictsBuilder.build();
    }

    public boolean resolveDescriptionChangesConflict(@Nonnull ResolveDescriptionConflictCommand command)
    {
        Args.notNull(command, "Resolve description conflict command is required.");
        ProjectId projectId = command.projectId();
        Architector architector = command.architector();
        FileId fileId = command.fileId();
        Project project = findProject(projectId, architector);
        File foundFile = findFile(project, fileId);
        Long projectCommitId = project.currentCommitId();
        if (projectCommitId == null)
        {
            throw new IllegalStateException("Project must have some changes.");
        }
        // TODO add check projectCommitId and command head commit id
        FileDescription fixedDescription = command.fixedDescription();
        FileDescriptionChanges newChanges = FileDiffCalculator.defineDescriptionChanges(
            foundFile.description(), fixedDescription
        );
        if (!newChanges.isEmpty())
        {
            CommitDescription commitData = CommitDescription.builder()
                .files(
                    singletonList(
                        CommitFileItem.of(
                            fileId,
                            foundFile.isoId(),
                            foundFile.schema(),
                            FileMetadataChanges.empty(),
                            newChanges,
                            List.of()
                        )
                    )
                )
                .build();
            Long commitId = commitChanges(
                project.projectId(),
                projectCommitId,
                architector.email(),
                "File " + fileId.id() + " description conflict was resolved.",
                commitData
            );
            foundFile.updateDescription(fixedDescription);
            project.updateCurrentCommitId(commitId);
            this.projectRepository.saveAndFlush(project);
        }
        return true;
    }

    @Nonnull
    private Project findProject(@Nonnull ProjectId projectId, @Nonnull Architector architector)
    {
        Project project = this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        if (!project.canBeUpdated(architector))
        {
            throw new AccessRightsNotFoundException();
        } else
        {
            return project;
        }
    }

    @Nonnull
    private File findFile(@Nonnull Project project, @Nonnull FileId fileId)
    {
        return project.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(project.projectId(), fileId));
    }

    // TODO move to commit service
    @Nonnull
    private Long commitChanges(@Nonnull ProjectId projectId,
                               @Nullable Long headCommitId,
                               @Nonnull String author,
                               @Nonnull String commitMessage,
                               @Nonnull CommitDescription commitData)
    {
        Commit newCommit = Commit.builder()
            .parentId(headCommitId)
            .projectId(projectId)
            .message(commitMessage)
            .author(author)
            .data(commitData)
            .build();
        this.commitRepository.saveAndFlush(newCommit);
        return newCommit.id();
    }

    @Nonnull
    private static List<ContentChangesBlock> defineChangesBlocks(@Nonnull List<CommitItem> items)
    {
        List<ContentChangesBlock> changesBlocks = new LinkedList<>();
        CommitItem firstItem = items.get(0);
        boolean isInit = true;
        int startIndex = firstItem.position();
        int lastIndex = startIndex;
        List<CommitItem> lastItems = new LinkedList<>();
        for (CommitItem item : items)
        {
            if (!isInit && !isPreviousBlock(lastIndex, item))
            {
                changesBlocks.add(new ContentChangesBlock(startIndex, lastIndex, lastItems));
                lastItems = new LinkedList<>();
                startIndex = item.position();
            }
            isInit = false;
            lastIndex = item.position();
            lastItems.add(item);
        }
        changesBlocks.add(new ContentChangesBlock(startIndex, lastIndex, lastItems));
        return changesBlocks;
    }

    private static boolean isPreviousBlock(int previousIndex,
                                           @Nonnull CommitItem nextItem)
    {
        return nextItem.type() == ChangeType.ADDITION
               ? previousIndex == nextItem.position()
               : previousIndex + 1 == nextItem.position();
    }

    private static boolean areIntersected(@Nonnull List<ContentChangesBlock> headChangesBlocks,
                                          @Nonnull List<ContentChangesBlock> newChangesBlocks)
    {
        int headIndex = 0;
        int newIndex = 0;
        while (headIndex < headChangesBlocks.size() && newIndex < newChangesBlocks.size())
        {
            // TODO
            ContentChangesBlock headBlock = headChangesBlocks.get(headIndex);
            ContentChangesBlock newBlock = newChangesBlocks.get(headIndex);
            if (headBlock.endIndex() >= newBlock.startIndex() && headBlock.startIndex() <= newBlock.startIndex() ||
                newBlock.endIndex() >= headBlock.startIndex() && newBlock.startIndex() <= headBlock.startIndex())
            {
                return true;
            }
            if (headBlock.endIndex() < newBlock.startIndex())
            {
                headIndex++;
            } else
            {
                newIndex++;
            }
        }
        return false;
    }

}

