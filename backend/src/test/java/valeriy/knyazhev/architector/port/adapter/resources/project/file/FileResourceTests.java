package valeriy.knyazhev.architector.port.adapter.resources.project.file;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import valeriy.knyazhev.architector.application.project.file.FileManagementService;
import valeriy.knyazhev.architector.application.project.file.command.*;
import valeriy.knyazhev.architector.application.project.file.conflict.data.DescriptionConflictChanges;
import valeriy.knyazhev.architector.application.project.file.conflict.data.MetadataConflictChanges;
import valeriy.knyazhev.architector.application.project.file.conflict.exception.FileContentConflictException;
import valeriy.knyazhev.architector.application.project.file.conflict.exception.FileDescriptionConflictException;
import valeriy.knyazhev.architector.application.project.file.conflict.exception.FileMetadataConflictException;
import valeriy.knyazhev.architector.application.project.file.validation.IFCFileValidator;
import valeriy.knyazhev.architector.application.project.file.validation.InvalidFileContentException;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.factory.ProjectObjectFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RunWith(SpringRunner.class)
@WebMvcTest(FileResource.class)
public class FileResourceTests
{

    private static final String USER_EMAIL = "tony.stark@architector.ru";

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private FileManagementService managementService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldAddFileBySourceUrl()
        throws Exception
    {
        // given
        String fileUrl = "https://test.projects.ru/example.ifc";
        String command = "{\"sourceUrl\": \"" + fileUrl + "\"}";
        ProjectId projectId = ProjectId.nextId();
        when(this.managementService.addFile(any(AddFileFromUrlCommand.class)))
            .thenReturn(ProjectObjectFactory.sampleFile());

        // expect
        this.mockMvc.perform(post("/api/projects/{projectId}/files/source", projectId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldNotAddFileBySourceUrl()
        throws Exception
    {
        // given
        String fileUrl = "https://test.projects.ru/example.ifc";
        String command = "{\"sourceUrl\": \"" + fileUrl + "\"}";
        ProjectId projectId = ProjectId.nextId();
        when(this.managementService.addFile(any(AddFileFromUrlCommand.class)))
            .thenReturn(null);

        // expect
        this.mockMvc.perform(post("/api/projects/{projectId}/files/source", projectId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldAddFileByUploadFile()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        MockMultipartFile file = new MockMultipartFile("file", "/home/test.ifc", "text/plain", "".getBytes());
        MockHttpServletRequestBuilder requestBuilder = multipart("/api/projects/{projectId}/files/import", projectId
            .id())
            .file(file);
        when(this.managementService.addFile(any(AddFileFromUploadCommand.class)))
            .thenReturn(ProjectObjectFactory.sampleFile());

        // expect
        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldNotAddFileByUploadFile()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        MockMultipartFile file = new MockMultipartFile("file", "/home/test.ifc", "text/plain", "".getBytes());
        MockHttpServletRequestBuilder requestBuilder = multipart("/api/projects/{projectId}/files/import", projectId
            .id())
            .file(file);
        when(this.managementService.addFile(any(AddFileFromUploadCommand.class)))
            .thenReturn(null);

        // expect
        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldReturnFile()
        throws Exception
    {
        // given
        Project project = ProjectObjectFactory.projectWithFiles(USER_EMAIL);
        ProjectId projectId = project.projectId();
        long commitId = 2L;
        project.updateCurrentCommitId(commitId);
        FileId fileId = project.files().get(0).fileId();
        when(this.projectRepository.findByProjectId((eq(projectId)))).thenReturn(Optional.of(project));

        // expect
        this.mockMvc.perform(get("/api/projects/{projectId}/files/{fileId}", projectId.id(), fileId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileId").isString())
            .andExpect(jsonPath("$.schema").isString())
            .andExpect(jsonPath("$.isoId").isString())
            .andExpect(jsonPath("$.accessRights").isString())
            .andExpect(jsonPath("$.metadata").exists())
            .andExpect(jsonPath("$.metadata.name").isString())
            .andExpect(jsonPath("$.metadata.authors").isArray())
            .andExpect(jsonPath("$.metadata.organizations").isArray())
            .andExpect(jsonPath("$.metadata.timestamp").isString())
            .andExpect(jsonPath("$.metadata.preprocessorVersion").isString())
            .andExpect(jsonPath("$.metadata.originatingSystem").isString())
            .andExpect(jsonPath("$.metadata.authorization").isString())
            .andExpect(jsonPath("$.description").exists())
            .andExpect(jsonPath("$.description.descriptions").isArray())
            .andExpect(jsonPath("$.description.implementationLevel").isString())
            .andExpect(jsonPath("$.currentCommitId").value(commitId));
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldNotFoundFileIfProjectNotFound()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.projectRepository.findByProjectId((eq(projectId)))).thenReturn(Optional.empty());

        // expect
        this.mockMvc.perform(get("/api/projects/{projectId}/files/{fileId}", projectId.id(), fileId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldNotFoundFileIfNotExist()
        throws Exception
    {
        // given
        Project project = ProjectObjectFactory.emptyProject(USER_EMAIL);
        ProjectId projectId = project.projectId();
        FileId fileId = FileId.nextId();
        when(this.projectRepository.findByProjectId((eq(projectId)))).thenReturn(Optional.of(project));

        // expect
        this.mockMvc.perform(get("/api/projects/{projectId}/files/{fileId}", projectId.id(), fileId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldNotFoundFileIfForbidden()
        throws Exception
    {
        // given
        Project project = ProjectObjectFactory.emptyProject("incorrect.user@architector.ru");
        ProjectId projectId = project.projectId();
        FileId fileId = FileId.nextId();
        when(this.projectRepository.findByProjectId((eq(projectId)))).thenReturn(Optional.of(project));

        // expect
        this.mockMvc.perform(get("/api/projects/{projectId}/files/{fileId}", projectId.id(), fileId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldUpdateFileContent()
        throws Exception
    {
        // given
        String newContent = "";
        String commitMessage = "File content updated";
        String command = "{" +
                         "\"content\": \"" + newContent + "\"," +
                         "\"commitMessage\": \"" + commitMessage + "\"," +
                         "\"headCommitId\": \"" + 2L + "\"" +
                         "}";
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.managementService.updateFileContent(any(UpdateFileContentCommand.class)))
            .thenReturn(List.of());

        // expect
        this.mockMvc.perform(put("/api/projects/{projectId}/files/{fileId}/content", projectId.id(), fileId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.updatedRoots").isArray());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldNotUpdateFileContentIfInvalid()
        throws Exception
    {
        // given
        String newContent = "";
        String commitMessage = "File content updated";
        String command = "{" +
                         "\"content\": \"" + newContent + "\"," +
                         "\"commitMessage\": \"" + commitMessage + "\"," +
                         "\"headCommitId\": \"" + 2L + "\"" +
                         "}";
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.managementService.updateFileContent(any(UpdateFileContentCommand.class)))
            .thenThrow(new InvalidFileContentException(List.of()));

        // expect
        this.mockMvc.perform(put("/api/projects/{projectId}/files/{fileId}/content", projectId.id(), fileId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.invalidEntities").isArray());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldResolveFileContentConflict()
        throws Exception
    {
        // given
        String newContent = "";
        String commitMessage = "File content updated";
        long headCommitId = 2L;
        String command = "{" +
                         "\"content\": \"" + newContent + "\"," +
                         "\"commitMessage\": \"" + commitMessage + "\"," +
                         "\"headCommitId\": \"" + headCommitId + "\"" +
                         "}";
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.managementService.updateFileContent(any(UpdateFileContentCommand.class)))
            .thenThrow(new FileContentConflictException(List.of(), List.of(), headCommitId));

        // expect
        this.mockMvc.perform(put("/api/projects/{projectId}/files/{fileId}/content", projectId.id(), fileId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.oldContent").isArray())
            .andExpect(jsonPath("$.conflictBlocks").isArray())
            .andExpect(jsonPath("$.links").isMap())
            .andExpect(jsonPath("$.headCommitId").isNumber());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldUpdateFileDescription()
        throws Exception
    {
        // given
        String newDescriptions = "[]";
        String newImplementationLevel = "2;1";
        String commitMessage = "File description updated";
        String command = "{" +
                         "\"descriptions\": " + newDescriptions + "," +
                         "\"implementationLevel\": \"" + newImplementationLevel + "\"," +
                         "\"commitMessage\": \"" + commitMessage + "\"," +
                         "\"headCommitId\": \"" + 2L + "\"" +
                         "}";
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.managementService.updateFileDescription(any(UpdateFileDescriptionCommand.class)))
            .thenReturn(true);

        // expect
        this.mockMvc.perform(put("/api/projects/{projectId}/files/{fileId}/description", projectId.id(), fileId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldNotUpdateFileDescription()
        throws Exception
    {
        // given
        String newDescriptions = "[]";
        String newImplementationLevel = "2;1";
        String commitMessage = "File description updated";
        String command = "{" +
                         "\"descriptions\": " + newDescriptions + "," +
                         "\"implementationLevel\": \"" + newImplementationLevel + "\"," +
                         "\"commitMessage\": \"" + commitMessage + "\"," +
                         "\"headCommitId\": \"" + 2L + "\"" +
                         "}";
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.managementService.updateFileDescription(any(UpdateFileDescriptionCommand.class)))
            .thenReturn(false);

        // expect
        this.mockMvc.perform(put("/api/projects/{projectId}/files/{fileId}/description", projectId.id(), fileId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldResolveFileDescriptionConflict()
        throws Exception
    {
        // given
        String newDescriptions = "[]";
        String newImplementationLevel = "2;1";
        String commitMessage = "File description updated";
        long headCommitId = 2L;
        String command = "{" +
                         "\"descriptions\": " + newDescriptions + "," +
                         "\"implementationLevel\": \"" + newImplementationLevel + "\"," +
                         "\"commitMessage\": \"" + commitMessage + "\"," +
                         "\"headCommitId\": \"" + headCommitId + "\"" +
                         "}";
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.managementService.updateFileDescription(any(UpdateFileDescriptionCommand.class)))
            .thenThrow(new FileDescriptionConflictException(
                    DescriptionConflictChanges.builder().build(), headCommitId
                )
            );

        // expect
        this.mockMvc.perform(put("/api/projects/{projectId}/files/{fileId}/description", projectId.id(), fileId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.conflictData").isMap())
            .andExpect(jsonPath("$.links").isMap())
            .andExpect(jsonPath("$.headCommitId").isNumber());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldUpdateFileMetadata()
        throws Exception
    {
        // given
        String newName = "Walls 1";
        String newAuthors = "[\"Tony Stark\"]";
        String newOrganizations = "[\"Tony Stark\"]";
        LocalDateTime newTimestamp = LocalDateTime.now();
        String newPreprocessorVersion = "version";
        String newOriginatingSystem = "system";
        String newAuthorization = "authorization";
        String commitMessage = "File metadata updated";
        String command = "{" +
                         "\"name\": \"" + newName + "\"," +
                         "\"authors\": " + newAuthors + "," +
                         "\"organizations\": " + newOrganizations + "," +
                         "\"timestamp\": \"" + newTimestamp + "\"," +
                         "\"preprocessorVersion\": \"" + newPreprocessorVersion + "\"," +
                         "\"originatingSystem\": \"" + newOriginatingSystem + "\"," +
                         "\"authorization\": \"" + newAuthorization + "\"," +
                         "\"commitMessage\": \"" + commitMessage + "\"," +
                         "\"headCommitId\": \"" + 2L + "\"" +
                         "}";
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.managementService.updateFileMetadata(any(UpdateFileMetadataCommand.class)))
            .thenReturn(true);

        // expect
        this.mockMvc.perform(put("/api/projects/{projectId}/files/{fileId}/metadata", projectId.id(), fileId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldNotUpdateFileMetadata()
        throws Exception
    {
        // given
        String newName = "Walls 1";
        String newAuthors = "[\"Tony Stark\"]";
        String newOrganizations = "[\"Tony Stark\"]";
        LocalDateTime newTimestamp = LocalDateTime.now();
        String newPreprocessorVersion = "version";
        String newOriginatingSystem = "system";
        String newAuthorization = "authorization";
        String commitMessage = "File metadata updated";
        String command = "{" +
                         "\"name\": \"" + newName + "\"," +
                         "\"authors\": " + newAuthors + "," +
                         "\"organizations\": " + newOrganizations + "," +
                         "\"timestamp\": \"" + newTimestamp + "\"," +
                         "\"preprocessorVersion\": \"" + newPreprocessorVersion + "\"," +
                         "\"originatingSystem\": \"" + newOriginatingSystem + "\"," +
                         "\"authorization\": \"" + newAuthorization + "\"," +
                         "\"commitMessage\": \"" + commitMessage + "\"," +
                         "\"headCommitId\": \"" + 2L + "\"" +
                         "}";
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.managementService.updateFileMetadata(any(UpdateFileMetadataCommand.class)))
            .thenReturn(false);

        // expect
        this.mockMvc.perform(put("/api/projects/{projectId}/files/{fileId}/metadata", projectId.id(), fileId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldResolveFileMetadataConflict()
        throws Exception
    {
        // given
        String newName = "Walls 1";
        String newAuthors = "[\"Tony Stark\"]";
        String newOrganizations = "[\"Tony Stark\"]";
        LocalDateTime newTimestamp = LocalDateTime.now();
        String newPreprocessorVersion = "version";
        String newOriginatingSystem = "system";
        String newAuthorization = "authorization";
        String commitMessage = "File metadata updated";
        long headCommitId = 2L;
        String command = "{" +
                         "\"name\": \"" + newName + "\"," +
                         "\"authors\": " + newAuthors + "," +
                         "\"organizations\": " + newOrganizations + "," +
                         "\"timestamp\": \"" + newTimestamp + "\"," +
                         "\"preprocessorVersion\": \"" + newPreprocessorVersion + "\"," +
                         "\"originatingSystem\": \"" + newOriginatingSystem + "\"," +
                         "\"authorization\": \"" + newAuthorization + "\"," +
                         "\"commitMessage\": \"" + commitMessage + "\"," +
                         "\"headCommitId\": \"" + 2L + "\"" +
                         "}";
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.managementService.updateFileMetadata(any(UpdateFileMetadataCommand.class)))
            .thenThrow(new FileMetadataConflictException(
                    MetadataConflictChanges.builder().build(), headCommitId
                )
            );

        // expect
        this.mockMvc.perform(put("/api/projects/{projectId}/files/{fileId}/metadata", projectId.id(), fileId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.conflictData").isMap())
            .andExpect(jsonPath("$.links").isMap())
            .andExpect(jsonPath("$.headCommitId").isNumber());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldDeleteFile()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.managementService.deleteFile(any(DeleteFileCommand.class)))
            .thenReturn(true);

        // expect
        this.mockMvc.perform(delete("/api/projects/{projectId}/files/{fileId}", projectId.id(), fileId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldNotDeleteFile()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.managementService.deleteFile(any(DeleteFileCommand.class)))
            .thenReturn(false);

        // expect
        this.mockMvc.perform(delete("/api/projects/{projectId}/files/{fileId}", projectId.id(), fileId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldDownloadFile()
        throws Exception
    {
        // given
        Project project = ProjectObjectFactory.projectWithFiles(USER_EMAIL);
        ProjectId projectId = project.projectId();
        FileId fileId = project.files().get(0).fileId();
        when(this.projectRepository.findByProjectId((eq(projectId)))).thenReturn(Optional.of(project));

        // expect
        ResultActions result = this.mockMvc
            .perform(get("/api/projects/{projectId}/files/{fileId}/download", projectId.id(), fileId.id())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        result.andExpect(status().isOk());
        assertThat(result.andReturn().getResponse().getHeader("Content-Disposition")).isNotNull();
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldNotDownloadFileIfProjectNotFound()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.projectRepository.findByProjectId((eq(projectId)))).thenReturn(Optional.empty());

        // expect
        this.mockMvc.perform(get("/api/projects/{projectId}/files/{fileId}/download", projectId.id(), fileId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser(USER_EMAIL)
    public void shouldNotDownloadFileIfFileNotFound()
        throws Exception
    {
        // given
        Project project = ProjectObjectFactory.emptyProject(USER_EMAIL);
        ProjectId projectId = project.projectId();
        FileId fileId = FileId.nextId();
        when(this.projectRepository.findByProjectId((eq(projectId)))).thenReturn(Optional.of(project));

        // expect
        this.mockMvc.perform(get("/api/projects/{projectId}/files/{fileId}/download", projectId.id(), fileId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists());
    }

}
