package valeriy.knyazhev.architector.application.commit;

import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valeriy.knyazhev.architector.application.commit.command.FindCommitsCommand;
import valeriy.knyazhev.architector.application.commit.command.MakeFileProjectionCommand;
import valeriy.knyazhev.architector.application.commit.command.MakeProjectProjectionCommand;
import valeriy.knyazhev.architector.application.commit.data.changes.CommitChangesData;
import valeriy.knyazhev.architector.application.commit.data.changes.FileChangesData;
import valeriy.knyazhev.architector.application.commit.data.changes.FileDescriptionChangesData;
import valeriy.knyazhev.architector.application.commit.data.changes.FileMetadataChangesData;
import valeriy.knyazhev.architector.application.commit.data.history.AbstractHistoryData;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.*;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection.FileProjection;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static valeriy.knyazhev.architector.application.commit.data.changes.ChangedValue.changeValue;
import static valeriy.knyazhev.architector.application.commit.data.changes.ChangedValue.newValue;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Service
@Transactional
public class CommitQueryService
{

    private final CommitRepository commitRepository;

    private final ProjectRepository projectRepository;

    public CommitQueryService(@Nonnull CommitRepository commitRepository,
                              @Nonnull ProjectRepository projectRepository)
    {
        this.commitRepository = Args.notNull(commitRepository, "Commit repository is required.");
        this.projectRepository = Args.notNull(projectRepository, "Project repository is required.");
    }

    @Nonnull
    public AbstractHistoryData fetchProjectHistory(@Nonnull FindCommitsCommand command)
    {
        Args.notNull(command, "Find commit command is required.");
        ProjectId projectId = command.projectId();
        Project project = findProject(projectId);
        List<Commit> commits = this.commitRepository.findByProjectIdOrderByIdDesc(projectId);
        return command.constructHistory(project, commits);
    }

    @Nonnull
    public Projection fetchProjection(@Nonnull MakeProjectProjectionCommand command)
    {
        ProjectId projectId = command.projectId();
        return makeProjection(projectId, command.commitId());
    }

    @Nonnull
    public FileProjection fetchProjection(@Nonnull MakeFileProjectionCommand command)
    {
        ProjectId projectId = command.projectId();
        Projection projection = makeProjection(projectId, command.commitId());
        FileId fileId = command.fileId();
        return projection.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId));
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
        Projection projection = makeProjection(commitEntity.projectId(), commitId);
        String newName = commit.name();
        String newDescription = commit.description();
        return new CommitChangesData(
            newName != null ? changeValue(projection.name(), newName) : null,
            newDescription != null ? changeValue(projection.description(), newDescription) : null,
            commit.changedFiles().stream()
                .map(file -> constructFileChanges(file, projection))
                .collect(Collectors.toList())
        );
    }

    @Nonnull
    private FileChangesData constructFileChanges(@Nonnull CommitFileItem changes,
                                                 @Nonnull Projection projection)
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
                Collections.singletonList(
                    new FileChangesData.FileSection(
                        changes.items().stream()
                            .map(item -> new FileChangesData.SectionItem(item.position(), item
                                .value(), ChangeType.ADDITION))
                            .collect(toList())
                    )
                )
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
            // FIXME add mapping files
            Collections.emptyList()
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
    private Projection makeProjection(@Nonnull ProjectId projectId,
                                      long commitId)
    {
        findProject(projectId);
        List<Commit> history = extractHistoryForId(projectId, commitId);
        return CommitCombinator.combineCommits(history);
    }

    @Nonnull
    private Project findProject(@Nonnull ProjectId projectId)
    {
        return this.projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }

    @Nonnull
    private List<Commit> extractHistoryForId(@Nonnull ProjectId projectId,
                                             long commitId)
    {
        List<Commit> commits = this.commitRepository.findByProjectIdOrderById(projectId);
        //TODO optimize
        List<Long> identifiers = new LinkedList<>();
        Long lastParentCommitId = findCommitById(commits, commitId).parentId();
        identifiers.add(commitId);
        while (lastParentCommitId != null)
        {
            identifiers.add(lastParentCommitId);
            lastParentCommitId = findCommitById(commits, lastParentCommitId).parentId();
        }
        return commits.stream()
            .filter(commit -> identifiers.contains(commit.id()))
            .sorted(Comparator.comparingLong(Commit::id))
            .collect(toList());
    }

    @Nonnull
    private static Commit findCommitById(@Nonnull List<Commit> commits,
                                         long commitId)
    {
        return commits.stream()
            .filter(commit -> commitId == commit.id())
            .findFirst()
            .orElseThrow(() -> new CommitNotFoundException(commitId));
    }

}

