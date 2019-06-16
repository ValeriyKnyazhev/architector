package valeriy.knyazhev.architector.application.project.file.conflict;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.application.project.file.conflict.command.ResolveContentConflictCommand;
import valeriy.knyazhev.architector.application.project.file.conflict.command.ResolveDescriptionConflictCommand;
import valeriy.knyazhev.architector.application.project.file.conflict.command.ResolveMetadataConflictCommand;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictBlock;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictBlock.ContentChangesBlock;
import valeriy.knyazhev.architector.application.project.file.conflict.data.ContentConflictChanges;
import valeriy.knyazhev.architector.application.project.file.conflict.data.DescriptionConflictChanges;
import valeriy.knyazhev.architector.application.project.file.conflict.data.MetadataConflictChanges;
import valeriy.knyazhev.architector.domain.model.AccessRightsNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.CommitItem;
import valeriy.knyazhev.architector.domain.model.commit.CommitRepository;
import valeriy.knyazhev.architector.domain.model.commit.FileDescriptionChanges;
import valeriy.knyazhev.architector.domain.model.commit.FileMetadataChanges;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.factory.ProjectObjectFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static valeriy.knyazhev.architector.factory.ArchitectorObjectFactory.createArchitector;
import static valeriy.knyazhev.architector.factory.CommitObjectFactory.sampleDescriptionChanges;
import static valeriy.knyazhev.architector.factory.CommitObjectFactory.sampleMetadataChanges;
import static valeriy.knyazhev.architector.factory.ProjectObjectFactory.*;

/**
 * @author Valeriy Knyazhev
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ResolveChangesConflictService.class)
public class ResolveChangesConflictServiceTests
{

    private final static String USER_EMAIL = "user@architector.ru";

    @Autowired
    private ResolveChangesConflictService resolveConflictService;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private CommitRepository commitRepository;

    @Test
    public void shouldNoConflictsIfOnlyHeadItemsExist()
    {
        // given
        List<String> content = generateContent();
        List<CommitItem> headItems = List.of();
        List<CommitItem> newItems = List.of(CommitItem.addItem("new", 1));

        // when
        ContentConflictChanges changes = this.resolveConflictService.checkContentChangesConflicts(
            content, headItems, newItems
        );

        // then
        assertThat(changes.isEmpty()).isTrue();
    }

    @Test
    public void shouldNoConflictsIfOnlyNewItemsExist()
    {
        // given
        List<String> content = generateContent();
        List<CommitItem> headItems = List.of(CommitItem.addItem("new", 1));
        List<CommitItem> newItems = List.of();

        // when
        ContentConflictChanges changes = this.resolveConflictService.checkContentChangesConflicts(
            content, headItems, newItems
        );

        // then
        assertThat(changes.isEmpty()).isTrue();
    }

    @Test
    public void shouldNoConflictsIfBlocksAreNotIntersected()
    {
        // given
        List<String> content = generateContent();
        List<CommitItem> headItems = List.of(CommitItem.addItem("new", 1));
        List<CommitItem> newItems = List.of(CommitItem.addItem("new", 4));

        // when
        ContentConflictChanges changes = this.resolveConflictService.checkContentChangesConflicts(
            content, headItems, newItems
        );

        // then
        assertThat(changes.isEmpty()).isTrue();
    }

    @Test
    public void shouldConflictsIfDeletedBlocksAreIntersected()
    {
        // given
        List<String> content = generateContent();
        List<CommitItem> headItems = List.of(
            CommitItem.deleteItem("4", 3),
            CommitItem.deleteItem("5", 4),
            CommitItem.deleteItem("6", 5)
        );
        List<CommitItem> newItems = List.of(
            CommitItem.deleteItem("4", 3),
            CommitItem.deleteItem("6", 5)
        );

        // when
        ContentConflictChanges changes = this.resolveConflictService.checkContentChangesConflicts(
            content, headItems, newItems
        );

        // then
        assertThat(changes.isEmpty()).isFalse();
        List<ContentConflictBlock> conflictBlocks = changes.conflictBlocks();
        assertThat(conflictBlocks).size().isEqualTo(1);
        ContentConflictBlock conflictBlock = conflictBlocks.get(0);
        List<ContentChangesBlock> headBlocks = conflictBlock.headBlocks();
        List<ContentChangesBlock> newBlocks = conflictBlock.newBlocks();
        assertThat(headBlocks).size().isEqualTo(1);
        assertThat(newBlocks).size().isEqualTo(2);
    }

    @Test
    public void shouldResolveFileContentConflict()
    {
        // given
        Project project = projectWithFiles("author");
        ProjectId projectId = project.projectId();
        project.updateCurrentCommitId(3L);
        FileId fileId = project.files().get(0).fileId();
        long headCommitId = 2L;
        String content = "new";
        Architector architector = new Architector();
        architector.setEmail("author");
        ResolveContentConflictCommand command = new ResolveContentConflictCommand(
            projectId.id(), fileId.id(), architector, headCommitId, content
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // when
        boolean resolved = this.resolveConflictService.resolveContentChangesConflict(command);

        // then
        assertThat(resolved).isTrue();
    }

    @Test
    public void shouldNotResolveFileContentConflictIfProjectNotFound()
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        long headCommitId = 2L;
        String content = "new";
        Architector architector = new Architector();
        architector.setEmail("author");
        ResolveContentConflictCommand command = new ResolveContentConflictCommand(
            projectId.id(), fileId.id(), architector, headCommitId, content
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> this.resolveConflictService.resolveContentChangesConflict(command))
            .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    public void shouldNotResolveFileContentConflictIfFileNotFound()
    {
        // given
        Project project = ProjectObjectFactory.emptyProject("author");
        ProjectId projectId = project.projectId();
        project.updateCurrentCommitId(3L);
        FileId fileId = FileId.nextId();
        long headCommitId = 2L;
        String content = "new";
        Architector architector = new Architector();
        architector.setEmail("author");
        ResolveContentConflictCommand command = new ResolveContentConflictCommand(
            projectId.id(), fileId.id(), architector, headCommitId, content
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() -> this.resolveConflictService.resolveContentChangesConflict(command))
            .isExactlyInstanceOf(FileNotFoundException.class);
    }

    @Test
    public void shouldNotResolveFileContentConflictIfProjectCommitIdNotExist()
    {
        // given
        Project project = projectWithFiles("author");
        ProjectId projectId = project.projectId();
        FileId fileId = project.files().get(0).fileId();
        long headCommitId = 2L;
        String content = "new";
        Architector architector = new Architector();
        architector.setEmail("author");
        ResolveContentConflictCommand command = new ResolveContentConflictCommand(
            projectId.id(), fileId.id(), architector, headCommitId, content
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() -> this.resolveConflictService.resolveContentChangesConflict(command))
        .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void shouldFindMetadataConflicts()
    {
        // given
        FileMetadata oldMetadata = sampleMetadata("");
        FileMetadataChanges headChanges = sampleMetadataChanges("head");
        FileMetadataChanges newChanges = sampleMetadataChanges("new");

        // when
        MetadataConflictChanges conflicts = this.resolveConflictService.checkMetadataChangesConflicts(
            oldMetadata, headChanges, newChanges
        );

        // then
        assertThat(conflicts.isEmpty()).isFalse();
    }

    @Test
    public void shouldFindMetadataConflictsIfHeadChanges()
    {
        // given
        FileMetadata oldMetadata = sampleMetadata("");
        FileMetadataChanges headChanges = sampleMetadataChanges("head");
        FileMetadataChanges newChanges = FileMetadataChanges.empty();

        // when
        MetadataConflictChanges conflicts = this.resolveConflictService.checkMetadataChangesConflicts(
            oldMetadata, headChanges, newChanges
        );

        // then
        assertThat(conflicts.isEmpty()).isFalse();
    }

    @Test
    public void shouldFindMetadataConflictsIfNewChanges()
    {
        // given
        FileMetadata oldMetadata = sampleMetadata("");
        FileMetadataChanges headChanges = FileMetadataChanges.empty();
        FileMetadataChanges newChanges = sampleMetadataChanges("new");

        // when
        MetadataConflictChanges conflicts = this.resolveConflictService.checkMetadataChangesConflicts(
                oldMetadata, headChanges, newChanges
            );

        // then
        assertThat(conflicts.isEmpty()).isFalse();
    }

    @Test
    public void shouldNotFindMetadataConflicts()
    {
        // given
        FileMetadata oldMetadata = sampleMetadata("");
        FileMetadataChanges headChanges = FileMetadataChanges.empty();
        FileMetadataChanges newChanges = FileMetadataChanges.empty();

        // when
        MetadataConflictChanges conflicts = this.resolveConflictService.checkMetadataChangesConflicts(
            oldMetadata, headChanges, newChanges
        );

        // then
        assertThat(conflicts.isEmpty()).isTrue();
    }

    @Test
    public void shouldFindDescriptionConflicts()
    {
        // given
        FileDescription oldDescription = sampleDescription("");
        FileDescriptionChanges headChanges = sampleDescriptionChanges("head");
        FileDescriptionChanges newChanges = sampleDescriptionChanges("new");

        // when
        DescriptionConflictChanges conflicts = this.resolveConflictService.checkDescriptionChangesConflicts(
            oldDescription, headChanges, newChanges
        );

        // then
        assertThat(conflicts.isEmpty()).isFalse();
    }

    @Test
    public void shouldFindDescriptionConflictsIfHeadChanges()
    {
        // given
        FileDescription oldDescription = sampleDescription("");
        FileDescriptionChanges headChanges = sampleDescriptionChanges("head");
        FileDescriptionChanges newChanges = FileDescriptionChanges.empty();

        // when
        DescriptionConflictChanges conflicts = this.resolveConflictService.checkDescriptionChangesConflicts(
            oldDescription, headChanges, newChanges
        );

        // then
        assertThat(conflicts.isEmpty()).isFalse();
    }

    @Test
    public void shouldFindDescriptionConflictsIfNewChanges()
    {
        // given
        FileDescription oldDescription = sampleDescription("");
        FileDescriptionChanges headChanges = FileDescriptionChanges.empty();
        FileDescriptionChanges newChanges = sampleDescriptionChanges("new");

        // when
        DescriptionConflictChanges conflicts = this.resolveConflictService
            .checkDescriptionChangesConflicts(oldDescription, headChanges, newChanges);

        // then
        assertThat(conflicts.isEmpty()).isFalse();
    }

    @Test
    public void shouldNotFindDescriptionConflicts()
    {
        // given
        FileDescription oldDescription = sampleDescription("");
        FileDescriptionChanges headChanges = FileDescriptionChanges.empty();
        FileDescriptionChanges newChanges = FileDescriptionChanges.empty();

        // when
        DescriptionConflictChanges conflicts = this.resolveConflictService
            .checkDescriptionChangesConflicts(oldDescription, headChanges, newChanges);

        // then
        assertThat(conflicts.isEmpty()).isTrue();
    }

    @Test
    public void shouldResolveMetadataConflicts()
    {
        // given
        Project project = projectWithFiles(USER_EMAIL);
        ProjectId projectId = project.projectId();
        FileId fileId = project.files().stream().findFirst().get().fileId();
        FileMetadata metadata = sampleMetadata("");
        ResolveMetadataConflictCommand command = buildMetadataCommand(projectId, fileId, metadata);
        project.updateCurrentCommitId(1L);
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // when
        boolean result = this.resolveConflictService.resolveMetadataChangesConflict(command);

        // then
        assertThat(result).isTrue();
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldSkipResolveMetadataConflictsIfNotChanged()
    {
        // given
        Project project = projectWithFiles(USER_EMAIL);
        ProjectId projectId = project.projectId();
        File file = project.files().stream().findFirst().get();
        FileId fileId = file.fileId();
        FileMetadata metadata = file.metadata();
        ResolveMetadataConflictCommand command = buildMetadataCommand(projectId, fileId, metadata);
        project.updateCurrentCommitId(1L);
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // when
        boolean result = this.resolveConflictService.resolveMetadataChangesConflict(command);

        // then
        assertThat(result).isTrue();
        verify(this.projectRepository, times(0)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldNotResolveMetadataConflictsIfProjectNotFound()
    {
        // given
        Project project = projectWithFiles(USER_EMAIL);
        ProjectId projectId = project.projectId();
        FileId fileId = project.files().stream().findFirst().get().fileId();
        FileMetadata metadata = sampleMetadata("");
        ResolveMetadataConflictCommand command = buildMetadataCommand(projectId, fileId, metadata);
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() ->this.resolveConflictService.resolveMetadataChangesConflict(command))
        .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    public void shouldNotResolveMetadataConflictsIfForbidden()
    {
        // given
        Project project = projectWithFiles("other@architector.ru");
        ProjectId projectId = project.projectId();
        FileId fileId = project.files().stream().findFirst().get().fileId();
        FileMetadata metadata = sampleMetadata("");
        ResolveMetadataConflictCommand command = buildMetadataCommand(projectId, fileId, metadata);
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() ->this.resolveConflictService.resolveMetadataChangesConflict(command))
            .isExactlyInstanceOf(AccessRightsNotFoundException.class);
    }

    @Test
    public void shouldNotResolveMetadataConflictsIfFileNotFound()
    {
        // given
        Project project = projectWithFiles(USER_EMAIL);
        ProjectId projectId = project.projectId();
        FileMetadata metadata = sampleMetadata("");
        ResolveMetadataConflictCommand command = buildMetadataCommand(projectId, FileId.nextId(), metadata);
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() ->this.resolveConflictService.resolveMetadataChangesConflict(command))
            .isExactlyInstanceOf(FileNotFoundException.class);
    }

    @Test
    public void shouldNotResolveMetadataConflictsIfCurrentCommitIdNotExist()
    {
        // given
        Project project = projectWithFiles(USER_EMAIL);
        ProjectId projectId = project.projectId();
        FileId fileId = project.files().stream().findFirst().get().fileId();
        FileMetadata metadata = sampleMetadata("");
        ResolveMetadataConflictCommand command = buildMetadataCommand(projectId, fileId, metadata);
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() ->this.resolveConflictService.resolveMetadataChangesConflict(command))
            .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void shouldResolveDescriptionConflicts()
    {
        // given
        Project project = projectWithFiles(USER_EMAIL);
        ProjectId projectId = project.projectId();
        FileId fileId = project.files().stream().findFirst().get().fileId();
        FileDescription description = sampleDescription("");
        ResolveDescriptionConflictCommand command = buildDescriptionCommand(projectId, fileId, description);
        project.updateCurrentCommitId(1L);
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // when
        boolean result = this.resolveConflictService.resolveDescriptionChangesConflict(command);

        // then
        assertThat(result).isTrue();
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldSkipResolveDescriptionConflictsIfNotChanged()
    {
        // given
        Project project = projectWithFiles(USER_EMAIL);
        ProjectId projectId = project.projectId();
        File file = project.files().stream().findFirst().get();
        FileId fileId = file.fileId();
        FileDescription description = file.description();
        ResolveDescriptionConflictCommand command = buildDescriptionCommand(projectId, fileId, description);
        project.updateCurrentCommitId(1L);
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // when
        boolean result = this.resolveConflictService.resolveDescriptionChangesConflict(command);

        // then
        assertThat(result).isTrue();
        verify(this.projectRepository, times(0)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldNotResolveDescriptionConflictsIfProjectNotFound()
    {
        // given
        Project project = projectWithFiles(USER_EMAIL);
        ProjectId projectId = project.projectId();
        FileId fileId = project.files().stream().findFirst().get().fileId();
        FileDescription description = sampleDescription("");
        ResolveDescriptionConflictCommand command = buildDescriptionCommand(projectId, fileId, description);
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() ->this.resolveConflictService.resolveDescriptionChangesConflict(command))
            .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    public void shouldNotResolveDescriptionConflictsIfForbidden()
    {
        // given
        Project project = projectWithFiles("other@architector.ru");
        ProjectId projectId = project.projectId();
        FileId fileId = project.files().stream().findFirst().get().fileId();
        FileDescription description = sampleDescription("");
        ResolveDescriptionConflictCommand command = buildDescriptionCommand(projectId, fileId, description);
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() ->this.resolveConflictService.resolveDescriptionChangesConflict(command))
            .isExactlyInstanceOf(AccessRightsNotFoundException.class);
    }

    @Test
    public void shouldNotResolveDescriptionConflictsIfFileNotFound()
    {
        // given
        Project project = projectWithFiles(USER_EMAIL);
        ProjectId projectId = project.projectId();
        FileDescription description = sampleDescription("");
        ResolveDescriptionConflictCommand command = buildDescriptionCommand(projectId, FileId.nextId(), description);
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() ->this.resolveConflictService.resolveDescriptionChangesConflict(command))
            .isExactlyInstanceOf(FileNotFoundException.class);
    }

    @Test
    public void shouldNotResolveDescriptionConflictsIfCurrentCommitIdNotExist()
    {
        // given
        Project project = projectWithFiles(USER_EMAIL);
        ProjectId projectId = project.projectId();
        FileId fileId = project.files().stream().findFirst().get().fileId();
        FileDescription description = sampleDescription("");
        ResolveDescriptionConflictCommand command = buildDescriptionCommand(projectId, fileId, description);
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() ->this.resolveConflictService.resolveDescriptionChangesConflict(command))
            .isExactlyInstanceOf(IllegalStateException.class);
    }

    private static List<String> generateContent()
    {
        return IntStream.rangeClosed(1, 10)
            .mapToObj(Integer::toString)
            .collect(Collectors.toList());
    }

    private static ResolveMetadataConflictCommand buildMetadataCommand(ProjectId projectId,
                                                                       FileId fileId,
                                                                       FileMetadata metadata)
    {
        return ResolveMetadataConflictCommand.builder()
            .projectId(projectId.id())
            .fileId(fileId.id())
            .architector(createArchitector(USER_EMAIL))
            .headCommitId(2L)
            .name(metadata.name())
            .authors(metadata.authors())
            .organizations(metadata.organizations())
            .timestamp(metadata.timestamp())
            .preprocessorVersion(metadata.preprocessorVersion())
            .originatingSystem(metadata.originatingSystem())
            .authorization(metadata.authorization())
            .build();
    }

    private static ResolveDescriptionConflictCommand buildDescriptionCommand(ProjectId projectId,
                                                                             FileId fileId,
                                                                             FileDescription description)
    {
        return ResolveDescriptionConflictCommand.builder()
            .projectId(projectId.id())
            .fileId(fileId.id())
            .architector(createArchitector(USER_EMAIL))
            .headCommitId(2L)
            .descriptions(description.descriptions())
            .implementationLevel(description.implementationLevel())
            .build();
    }

}