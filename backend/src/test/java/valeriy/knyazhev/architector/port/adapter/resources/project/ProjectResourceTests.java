package valeriy.knyazhev.architector.port.adapter.resources.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import valeriy.knyazhev.architector.application.project.ProjectData;
import valeriy.knyazhev.architector.application.project.ProjectManagementService;
import valeriy.knyazhev.architector.application.project.ProjectQueryService;
import valeriy.knyazhev.architector.application.project.command.*;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static valeriy.knyazhev.architector.port.adapter.resources.project.request.AddAccessRightsToProjectRequest.AccessRights.READ;
import static valeriy.knyazhev.architector.port.adapter.resources.project.request.AddAccessRightsToProjectRequest.AccessRights.WRITE;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ProjectResource.class)
public class ProjectResourceTests
{

    @MockBean
    private ProjectManagementService managementService;

    @MockBean
    private ProjectQueryService queryService;

    @Autowired
    protected MockMvc mockMvc;

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldCreateProject()
        throws Exception
    {
        // given
        String projectName = "Project";
        String projectDescription = "Description";
        String command = "{" +
                         "\"name\":\"" + projectName + "\"," +
                         "\"description\":\"" + projectDescription + "\"" +
                         "}";
        when(this.managementService.createProject(any(CreateProjectCommand.class)))
            .thenReturn(ProjectId.nextId());

        // expect
        this.mockMvc.perform(post("/api/projects/")
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldUpdateProject()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        String projectName = "new name";
        String projectDescription = "new description";
        String command = "{" +
                         "\"name\":\"" + projectName + "\"," +
                         "\"description\":\"" + projectDescription + "\"" +
                         "}";
        when(this.managementService.updateProjectData(any(Architector.class), any(UpdateProjectDataCommand.class)))
            .thenReturn(true);

        // expect
        this.mockMvc.perform(put("/api/projects/{projectId}", projectId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldReturnProject()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        when(this.queryService.findById(eq(projectId.id()), any(Architector.class)))
            .thenReturn(sampleProject(projectId));

        // expect
        this.mockMvc.perform(get("/api/projects/{projectId}", projectId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(projectId.id()))
            .andExpect(jsonPath("$.files").exists());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldNotFoundProject()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        when(this.queryService.findById(eq(projectId.id()), any(Architector.class)))
            .thenReturn(null);

        // expect
        this.mockMvc.perform(get("/api/projects/{projectId}", projectId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldReturnListProjects()
        throws Exception
    {
        // given
        when(this.queryService.findProjects(any(Architector.class)))
            .thenReturn(List.of(sampleProject(ProjectId.nextId())));

        // expect
        this.mockMvc.perform(get("/api/projects")
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projects").isArray());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldGrantProjectReadAccessRights()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        String user = "Project";
        String command = "{" +
                         "\"email\":\"" + user + "\"," +
                         "\"accessRights\":\"" + READ + "\"" +
                         "}";
        // expect
        this.mockMvc.perform(post("/api/projects/{projectId}/access-rights", projectId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
        verify(this.managementService, times(1))
            .addReadAccessRights(any(Architector.class), any(AddReadAccessRightsCommand.class));
        verify(this.managementService, times(0))
            .addWriteAccessRights(any(Architector.class), any(AddWriteAccessRightsCommand.class));
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldGrantProjectWriteAccessRights()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        String user = "Project";
        String command = "{" +
                         "\"email\":\"" + user + "\"," +
                         "\"accessRights\":\"" + WRITE + "\"" +
                         "}";
        // expect
        this.mockMvc.perform(post("/api/projects/{projectId}/access-rights", projectId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
        verify(this.managementService, times(0))
            .addReadAccessRights(any(Architector.class), any(AddReadAccessRightsCommand.class));
        verify(this.managementService, times(1))
            .addWriteAccessRights(any(Architector.class), any(AddWriteAccessRightsCommand.class));
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldNotGrantProjectIncorrectAccessRights()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        String user = "Project";
        String incorrectAccessRights = "INCORRECT";
        String command = "{" +
                         "\"email\":\"" + user + "\"," +
                         "\"accessRights\":\"" + incorrectAccessRights + "\"" +
                         "}";
        // expect
        this.mockMvc.perform(post("/api/projects/{projectId}/access-rights", projectId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isBadRequest());
        verify(this.managementService, times(0))
            .addReadAccessRights(any(Architector.class), any(AddReadAccessRightsCommand.class));
        verify(this.managementService, times(0))
            .addWriteAccessRights(any(Architector.class), any(AddWriteAccessRightsCommand.class));
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldTakeAwayProjectAccessRights()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        String user = "Project";
        String command = "{" +
                         "\"email\":\"" + user + "\"" +
                         "}";
        // expect
        this.mockMvc.perform(delete("/api/projects/{projectId}/access-rights", projectId.id())
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
        verify(this.managementService, times(1))
            .takeAwayAccessRights(any(Architector.class), any(TakeAwayAccessRightsCommand.class));
    }

    private static ProjectData sampleProject(ProjectId projectId)
    {
        LocalDateTime now = LocalDateTime.now();
        return ProjectData.builder()
            .projectId(projectId.id())
            .author("tony.stark@architector.ru")
            .name("Project")
            .description("Some description")
            .createdDate(now)
            .updatedDate(now)
            .accessRights(ProjectAccessRights.OWNER)
            .files(List.of())
            .build();
    }

}
