package valeriy.knyazhev.architector.application.project.file;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.application.commit.ProjectionConstructService;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.command.*;
import valeriy.knyazhev.architector.application.project.file.conflict.ResolveChangesConflictService;
import valeriy.knyazhev.architector.application.project.file.conflict.exception.FileContentConflictException;
import valeriy.knyazhev.architector.application.project.file.conflict.exception.FileDescriptionConflictException;
import valeriy.knyazhev.architector.application.project.file.conflict.exception.FileMetadataConflictException;
import valeriy.knyazhev.architector.application.project.file.validation.ChangedEntity;
import valeriy.knyazhev.architector.application.project.file.validation.IFCFileValidator;
import valeriy.knyazhev.architector.application.project.file.validation.IFCFileValidator.ValidationType;
import valeriy.knyazhev.architector.application.util.ContentReadingException;
import valeriy.knyazhev.architector.domain.model.AccessRightsNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.Commit;
import valeriy.knyazhev.architector.domain.model.commit.CommitRepository;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.factory.ProjectObjectFactory;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static valeriy.knyazhev.architector.factory.ArchitectorObjectFactory.createArchitector;
import static valeriy.knyazhev.architector.factory.ProjectObjectFactory.*;

/**
 * @author Valeriy Knyazhev
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileManagementService.class)
public class FileManagementServiceTests
{

    private final static String USER_EMAIL = "tony.stark@architector.ru";

    @Autowired
    private FileManagementService managementService;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private ProjectionConstructService projectionConstructService;

    @MockBean
    private IFCFileReader fileReader;

    @MockBean
    private IFCFileValidator validator;

    @MockBean
    private CommitRepository commitRepository;

    @MockBean
    private ResolveChangesConflictService conflictService;

    @Test
    public void shouldAddNewFileFromSource()
    {
        // given
        Architector architector = createArchitector(USER_EMAIL);
        Project project = emptyProject(architector.email());
        FileData file = sampleFile();
        AddFileFromUrlCommand command = new AddFileFromUrlCommand(
            project.projectId().id(), architector, "https://test.com/test.ifc"
        );
        when(this.fileReader.readFromUrl(any(URL.class))).thenReturn(file);
        when(this.projectRepository.findByProjectId(project.projectId())).thenReturn(Optional.of(project));

        // when
        File newFile = this.managementService.addFile(command);

        // then
        assertThat(newFile).isNotNull();
        assertThat(newFile.schema()).isEqualTo(file.schema());
        assertThat(newFile.isoId()).isEqualTo(file.isoId());
        assertThat(newFile.metadata()).isEqualTo(file.metadata());
        assertThat(newFile.description()).isEqualTo(file.description());
        assertThat(newFile.content()).isEqualTo(file.content());
        verify(this.commitRepository, times(1)).saveAndFlush(any(Commit.class));
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldNotAddNewFileFromSourceIfProjectNotFound()
    {
        // given
        Architector architector = createArchitector(USER_EMAIL);
        ProjectId projectId = ProjectId.nextId();
        FileData file = sampleFile();
        AddFileFromUrlCommand command = new AddFileFromUrlCommand(
            projectId.id(), architector, "https://test.com/test.ifc"
        );
        when(this.fileReader.readFromUrl(any(URL.class))).thenReturn(file);
        when(this.projectRepository.findByProjectId(projectId)).thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> this.managementService.addFile(command))
            .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    public void shouldNotAddNewFileFromSourceIfForbidden()
    {
        // given
        Architector architector = createArchitector(USER_EMAIL);
        Project project = emptyProject("other@architector.ru");
        FileData file = sampleFile();
        AddFileFromUrlCommand command = new AddFileFromUrlCommand(
            project.projectId().id(), architector, "https://test.com/test.ifc"
        );
        when(this.fileReader.readFromUrl(any(URL.class))).thenReturn(file);
        when(this.projectRepository.findByProjectId(project.projectId())).thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() -> this.managementService.addFile(command))
            .isExactlyInstanceOf(AccessRightsNotFoundException.class);
    }

    @Test
    public void shouldNotAddNewFileFromSourceIfFileNotRead()
    {
        // given
        Architector architector = createArchitector(USER_EMAIL);
        ProjectId projectId = ProjectId.nextId();
        AddFileFromUrlCommand command = new AddFileFromUrlCommand(
            projectId.id(), architector, "https://test.com/test.ifc"
        );
        when(this.fileReader.readFromUrl(any(URL.class))).thenThrow(new ContentReadingException());

        // expect
        assertThatThrownBy(() -> this.managementService.addFile(command))
            .isExactlyInstanceOf(ContentReadingException.class);
    }

    @Test
    public void shouldDeleteFile()
    {
        // given
        Architector architector = createArchitector(USER_EMAIL);
        Project project = projectWithFiles(architector.email());
        File file = project.files().stream().findFirst().get();
        DeleteFileCommand command = new DeleteFileCommand(
            project.projectId().id(), file.fileId().id(), architector
        );
        when(this.projectRepository.findByProjectId(project.projectId())).thenReturn(Optional.of(project));

        // when
        boolean result = this.managementService.deleteFile(command);

        // then
        assertThat(result).isTrue();
        verify(this.commitRepository, times(1)).saveAndFlush(any(Commit.class));
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldNotDeleteFileIfFileNotFound()
    {
        // given
        Architector architector = createArchitector(USER_EMAIL);
        Project project = emptyProject(architector.email());
        DeleteFileCommand command = new DeleteFileCommand(
            project.projectId().id(), FileId.nextId().id(), architector
        );
        when(this.projectRepository.findByProjectId(project.projectId())).thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() -> this.managementService.deleteFile(command))
            .isExactlyInstanceOf(FileNotFoundException.class);
    }

    @Test
    public void shouldUpdateFileDescription()
        throws FileDescriptionConflictException
    {
        // given
        Architector architector = createArchitector(USER_EMAIL);
        Project project = projectWithFiles(architector.email());
        File file = project.files().stream().findFirst().get();
        FileDescription newDescription = sampleDescription("new");
        long headCommitId = 2L;
        project.updateCurrentCommitId(headCommitId);
        UpdateFileDescriptionCommand command = UpdateFileDescriptionCommand.builder()
            .projectId(project.projectId().id())
            .fileId(file.fileId().id())
            .architector(architector)
            .descriptions(newDescription.descriptions())
            .implementationLevel(newDescription.implementationLevel())
            .headCommitId(headCommitId)
            .build();
        when(this.projectRepository.findByProjectId(project.projectId())).thenReturn(Optional.of(project));

        // when
        boolean result = this.managementService.updateFileDescription(command);

        // then
        assertThat(result).isTrue();
        verify(this.commitRepository, times(1)).saveAndFlush(any(Commit.class));
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldNotUpdateFileDescriptionIfProjectWithourChanges()
        throws FileDescriptionConflictException
    {
        // given
        Architector architector = createArchitector(USER_EMAIL);
        Project project = projectWithFiles(architector.email());
        File file = project.files().stream().findFirst().get();
        FileDescription newDescription = sampleDescription("new");
        long headCommitId = 2L;
        UpdateFileDescriptionCommand command = UpdateFileDescriptionCommand.builder()
            .projectId(project.projectId().id())
            .fileId(file.fileId().id())
            .architector(architector)
            .descriptions(newDescription.descriptions())
            .implementationLevel(newDescription.implementationLevel())
            .headCommitId(headCommitId)
            .build();
        when(this.projectRepository.findByProjectId(project.projectId())).thenReturn(Optional.of(project));

        // when
        assertThatThrownBy(()->this.managementService.updateFileDescription(command))
            .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void shouldUpdateFileMetadata()
        throws FileMetadataConflictException
    {
        // given
        Architector architector = createArchitector(USER_EMAIL);
        Project project = projectWithFiles(architector.email());
        File file = project.files().stream().findFirst().get();
        FileMetadata newMetadata = sampleMetadata("new");
        long headCommitId = 2L;
        project.updateCurrentCommitId(headCommitId);
        UpdateFileMetadataCommand command = UpdateFileMetadataCommand.builder()
            .projectId(project.projectId().id())
            .fileId(file.fileId().id())
            .architector(architector)
            .name(newMetadata.name())
            .authors(newMetadata.authors())
            .organizations(newMetadata.authors())
            .timestamp(newMetadata.timestamp())
            .preprocessorVersion(newMetadata.preprocessorVersion())
            .originatingSystem(newMetadata.originatingSystem())
            .authorization(newMetadata.authorization())
            .headCommitId(headCommitId)
            .build();
        when(this.projectRepository.findByProjectId(project.projectId())).thenReturn(Optional.of(project));

        // when
        boolean result = this.managementService.updateFileMetadata(command);

        // then
        assertThat(result).isTrue();
        verify(this.commitRepository, times(1)).saveAndFlush(any(Commit.class));
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldNotUpdateFileMetadataIfProjectWithoutChanges()
        throws FileMetadataConflictException
    {
        // given
        Architector architector = createArchitector(USER_EMAIL);
        Project project = projectWithFiles(architector.email());
        File file = project.files().stream().findFirst().get();
        FileMetadata newMetadata = sampleMetadata("new");
        long headCommitId = 2L;
        UpdateFileMetadataCommand command = UpdateFileMetadataCommand.builder()
            .projectId(project.projectId().id())
            .fileId(file.fileId().id())
            .architector(architector)
            .name(newMetadata.name())
            .authors(newMetadata.authors())
            .organizations(newMetadata.authors())
            .timestamp(newMetadata.timestamp())
            .preprocessorVersion(newMetadata.preprocessorVersion())
            .originatingSystem(newMetadata.originatingSystem())
            .authorization(newMetadata.authorization())
            .headCommitId(headCommitId)
            .build();
        when(this.projectRepository.findByProjectId(project.projectId())).thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(()->this.managementService.updateFileMetadata(command))
        .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void shouldUpdateFileContent()
        throws FileContentConflictException
    {
        // given
        Architector architector = createArchitector(USER_EMAIL);
        Project project = projectWithFiles(architector.email());
        File file = project.files().stream().findFirst().get();
        long headCommitId = 2L;
        project.updateCurrentCommitId(headCommitId);
        String content = "new\ncontent";
        UpdateFileContentCommand command = new UpdateFileContentCommand(
            project.projectId().id(),
            file.fileId().id(),
            architector,
            content,
            "new content",
            headCommitId
        );
        when(this.projectRepository.findByProjectId(project.projectId())).thenReturn(Optional.of(project));
        when(this.validator.validateContent(any(ValidationType.class), eq(file.schema()), any(), any()))
            .thenReturn(List.of());

        // when
        List<ChangedEntity> result = this.managementService.updateFileContent(command);

        // then
        assertThat(result).isEmpty();
        verify(this.commitRepository, times(1)).saveAndFlush(any(Commit.class));
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldNotUpdateFileContentIfProjectWithoutChanges()
        throws FileMetadataConflictException
    {
        // given
        Architector architector = createArchitector(USER_EMAIL);
        Project project = projectWithFiles(architector.email());
        File file = project.files().stream().findFirst().get();
        long headCommitId = 2L;
        String content = "new\ncontent";
        UpdateFileContentCommand command = new UpdateFileContentCommand(
            project.projectId().id(),
            file.fileId().id(),
            architector,
            content,
            "new content",
            headCommitId
        );
        when(this.projectRepository.findByProjectId(project.projectId())).thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(()->this.managementService.updateFileContent(command))
            .isExactlyInstanceOf(IllegalStateException.class);
    }

    private static FileData sampleFile()
    {
        File file = ProjectObjectFactory.sampleFile();
        return new FileData(
            file.schema(), file.isoId(), file.metadata(), file.description(), file.content()
        );
    }

}