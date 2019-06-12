package valeriy.knyazhev.architector.port.adapter.resources.commit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import valeriy.knyazhev.architector.application.commit.ChangesApplicationService;
import valeriy.knyazhev.architector.application.commit.CommitQueryService;
import valeriy.knyazhev.architector.application.commit.command.FetchChangesHistoryCommand;
import valeriy.knyazhev.architector.application.commit.command.FetchCommitChangesCommand;
import valeriy.knyazhev.architector.application.commit.command.MakeFileProjectionCommand;
import valeriy.knyazhev.architector.application.commit.command.MakeProjectProjectionCommand;
import valeriy.knyazhev.architector.application.commit.data.changes.CommitChangesData;
import valeriy.knyazhev.architector.application.commit.data.history.FileHistoryData;
import valeriy.knyazhev.architector.application.commit.data.history.ProjectHistoryData;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection.FileProjection;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static valeriy.knyazhev.architector.factory.ProjectObjectFactory.sampleDescription;
import static valeriy.knyazhev.architector.factory.ProjectObjectFactory.sampleMetadata;

/**
 * @author Valeriy Knyazhev
 */
@RunWith(SpringRunner.class)
@WebMvcTest(CommitResource.class)
public class CommitResourceTests
{

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private CommitQueryService commitQueryService;

    @MockBean
    private ChangesApplicationService changesApplicationService;

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldReturnProjectCommits()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        when(this.commitQueryService.fetchProjectHistory(any(FetchChangesHistoryCommand.class)))
            .thenReturn(new ProjectHistoryData(projectId.id(), List.of()));

        // expect
        this.mockMvc.perform(get("/api/projects/{projectId}/commits", projectId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(projectId.id()))
            .andExpect(jsonPath("$.commits").isArray());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldReturnFileCommits()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        when(this.commitQueryService.fetchProjectHistory(any(FetchChangesHistoryCommand.class)))
            .thenReturn(new FileHistoryData(fileId.id(), List.of()));

        // expect
        this.mockMvc.perform(get("/api/projects/{projectId}//files/{fileId}/commits",
            projectId.id(), fileId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileId").value(fileId.id()))
            .andExpect(jsonPath("$.commits").isArray());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldReturnProjectContentByCommit()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        String projectName = "project";
        String projectDescription = "description";
        long commitId = 2L;
        when(this.commitQueryService.fetchProjection(any(MakeProjectProjectionCommand.class)))
            .thenReturn(Projection.of(projectName, projectDescription, List.of()));

        // expect
        this.mockMvc.perform(get("/api/projects/{qProjectId}/commits/{commitId}/content",
            projectId.id(), commitId)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(projectId.id()))
            .andExpect(jsonPath("$.name").value(projectName))
            .andExpect(jsonPath("$.description").value(projectDescription))
            .andExpect(jsonPath("$.files").isArray());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldReturnFileContentByCommit()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        String isoId = "ISO-10303-21";
        String schema = "IFC4";
        long commitId = 2L;
        when(this.commitQueryService.fetchProjection(any(MakeFileProjectionCommand.class)))
            .thenReturn(
                FileProjection.of(
                    fileId,
                    isoId,
                    schema,
                    sampleMetadata(),
                    sampleDescription(),
                    List.of()
                )
            );

        // expect
        this.mockMvc.perform(get("/api/projects/{qProjectId}/files/{qFileId}/commits/{commitId}/content",
            projectId.id(), fileId.id(), commitId)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileId").value(fileId.id()))
            .andExpect(jsonPath("$.description").isMap())
            .andExpect(jsonPath("$.metadata").isMap())
            .andExpect(jsonPath("$.content").isString());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldNotReturnFileContentByCommitIfFileNotFound()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        String isoId = "ISO-10303-21";
        String schema = "IFC4";
        long commitId = 2L;
        when(this.commitQueryService.fetchProjection(any(MakeFileProjectionCommand.class)))
            .thenThrow(new FileNotFoundException(projectId, fileId));

        // expect
        this.mockMvc.perform(get("/api/projects/{qProjectId}/files/{qFileId}/commits/{commitId}/content",
            projectId.id(), fileId.id(), commitId)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void shouldDownloadFileContentByCommitId()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        String isoId = "ISO-10303-21";
        String schema = "IFC4";
        long commitId = 2L;
        when(this.commitQueryService.fetchProjection(any(MakeFileProjectionCommand.class)))
            .thenReturn(
                FileProjection.of(
                    fileId,
                    isoId,
                    schema,
                    sampleMetadata(),
                    sampleDescription(),
                    List.of()
                )
            );

        // expect
        ResultActions result = this.mockMvc
            .perform(get("/api/projects/{qProjectId}/files/{qFileId}/commits/{commitId}/download",
                projectId.id(), fileId.id(), commitId)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        result.andExpect(status().isOk());
        assertThat(result.andReturn().getResponse().getHeader("Content-Disposition")).isNotNull();
    }

    @Test
    public void shouldNotDownloadFileContentByCommitIdIfFileNotFound()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        FileId fileId = FileId.nextId();
        String isoId = "ISO-10303-21";
        String schema = "IFC4";
        long commitId = 2L;
        when(this.commitQueryService.fetchProjection(any(MakeFileProjectionCommand.class)))
            .thenThrow(new FileNotFoundException(projectId, fileId));

        // expect
        this.mockMvc
            .perform(get("/api/projects/{qProjectId}/files/{qFileId}/commits/{commitId}/download",
                projectId.id(), fileId.id(), commitId)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists());

    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldReturnProjectChangesByCommitId()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        String projectName = "name";
        String projectDescription = "description";
        long commitId = 2L;
        when(this.changesApplicationService.fetchCommitChanges(any(FetchCommitChangesCommand.class)))
            .thenReturn(new CommitChangesData(projectName, projectDescription, List.of()));

        // expect
        this.mockMvc.perform(get("/api/projects/{qProjectId}/commits/{commitId}/changes",
            projectId.id(), commitId)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(projectName))
            .andExpect(jsonPath("$.description").value(projectDescription))
            .andExpect(jsonPath("$.changedFiles").isArray());
    }

}