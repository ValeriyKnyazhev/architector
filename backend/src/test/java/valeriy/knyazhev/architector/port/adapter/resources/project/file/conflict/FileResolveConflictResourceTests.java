package valeriy.knyazhev.architector.port.adapter.resources.project.file.conflict;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import valeriy.knyazhev.architector.application.project.file.conflict.ResolveChangesConflictService;
import valeriy.knyazhev.architector.application.project.file.conflict.command.ResolveContentConflictCommand;
import valeriy.knyazhev.architector.application.project.file.conflict.command.ResolveDescriptionConflictCommand;
import valeriy.knyazhev.architector.application.project.file.conflict.command.ResolveMetadataConflictCommand;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Valeriy Knyazhev
 */
@RunWith(SpringRunner.class)
@WebMvcTest(FileResolveConflictResource.class)
public class FileResolveConflictResourceTests
{

    @Autowired
    protected MockMvc mockMvc;
    @MockBean
    private ResolveChangesConflictService resolveConflictService;

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldResolveFileContentConflict()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        String newContent = "";
        String commitMessage = "File content updated";
        String command = "{" +
                         "\"content\": \"" + newContent + "\"," +
                         "\"commitMessage\": \"" + commitMessage + "\"," +
                         "\"headCommitId\": \"" + 2L + "\"" +
                         "}";
        when(this.resolveConflictService.resolveContentChangesConflict(any(ResolveContentConflictCommand.class)))
            .thenReturn(true);

        // expect
        this.mockMvc
            .perform(post("/api/projects/{qProjectId}/files/{qFileId}/content/resolve-conflict",
                projectId.id(), fileId.id())
                .content(command)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldNotResolveFileContentConflict()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        String newContent = "";
        String commitMessage = "File content updated";
        String command = "{" +
                         "\"content\": \"" + newContent + "\"," +
                         "\"commitMessage\": \"" + commitMessage + "\"," +
                         "\"headCommitId\": \"" + 2L + "\"" +
                         "}";
        when(this.resolveConflictService.resolveContentChangesConflict(any(ResolveContentConflictCommand.class)))
            .thenReturn(false);

        // expect
        this.mockMvc
            .perform(post("/api/projects/{qProjectId}/files/{qFileId}/content/resolve-conflict",
                projectId.id(), fileId.id())
                .content(command)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldResolveFileDescriptionConflict()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        String newDescriptions = "[]";
        String newImplementationLevel = "2;1";
        String commitMessage = "File description updated";
        String command = "{" +
                         "\"descriptions\": " + newDescriptions + "," +
                         "\"implementationLevel\": \"" + newImplementationLevel + "\"," +
                         "\"commitMessage\": \"" + commitMessage + "\"," +
                         "\"headCommitId\": \"" + 2L + "\"" +
                         "}";
        when(this.resolveConflictService
            .resolveDescriptionChangesConflict(any(ResolveDescriptionConflictCommand.class)))
            .thenReturn(true);

        // expect
        this.mockMvc
            .perform(post("/api/projects/{qProjectId}/files/{qFileId}/description/resolve-conflict",
                projectId.id(), fileId.id())
                .content(command)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldNotResolveFileDescriptionConflict()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        String newDescriptions = "[]";
        String newImplementationLevel = "2;1";
        String commitMessage = "File description updated";
        String command = "{" +
                         "\"descriptions\": " + newDescriptions + "," +
                         "\"implementationLevel\": \"" + newImplementationLevel + "\"," +
                         "\"commitMessage\": \"" + commitMessage + "\"," +
                         "\"headCommitId\": \"" + 2L + "\"" +
                         "}";
        when(this.resolveConflictService
            .resolveDescriptionChangesConflict(any(ResolveDescriptionConflictCommand.class)))
            .thenReturn(false);

        // expect
        this.mockMvc
            .perform(post("/api/projects/{qProjectId}/files/{qFileId}/description/resolve-conflict",
                projectId.id(), fileId.id())
                .content(command)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldResolveFileMetadataConflict()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
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
        when(this.resolveConflictService
            .resolveMetadataChangesConflict(any(ResolveMetadataConflictCommand.class)))
            .thenReturn(true);

        // expect
        this.mockMvc
            .perform(post("/api/projects/{qProjectId}/files/{qFileId}/metadata/resolve-conflict",
                projectId.id(), fileId.id())
                .content(command)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldNotResolveFileMetadataConflict()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
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
        when(this.resolveConflictService
            .resolveMetadataChangesConflict(any(ResolveMetadataConflictCommand.class)))
            .thenReturn(false);

        // expect
        this.mockMvc
            .perform(post("/api/projects/{qProjectId}/files/{qFileId}/metadata/resolve-conflict",
                projectId.id(), fileId.id())
                .content(command)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }


}