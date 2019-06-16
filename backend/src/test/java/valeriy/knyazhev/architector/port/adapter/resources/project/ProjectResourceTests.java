package valeriy.knyazhev.architector.port.adapter.resources.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import valeriy.knyazhev.architector.application.project.AccessGrantedInfo;
import valeriy.knyazhev.architector.application.project.ProjectData;
import valeriy.knyazhev.architector.application.project.ProjectManagementService;
import valeriy.knyazhev.architector.application.project.ProjectQueryService;
import valeriy.knyazhev.architector.application.project.command.*;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.factory.ProjectObjectFactory;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static valeriy.knyazhev.architector.port.adapter.resources.project.request.AddAccessRightsToProjectRequest.AccessRights.READ;
import static valeriy.knyazhev.architector.port.adapter.resources.project.request.AddAccessRightsToProjectRequest.AccessRights.WRITE;
import static valeriy.knyazhev.architector.util.DocsUtil.restDocument;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@RunWith(SpringRunner.class)
@WebMvcTest(ProjectResource.class)
public class ProjectResourceTests
{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectManagementService managementService;

    @MockBean
    private ProjectQueryService queryService;

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
        this.mockMvc.perform(post("/api/projects")
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists())
            .andDo(
                restDocument(
                    "create-project",
                    requestFields(
                        fieldWithPath("name").description("Name for new project."),
                        fieldWithPath("description").description("Description of new project")
                    ),
                    responseFields(
                        fieldWithPath("info").description("Message about action success.")
                    )
                )
            );
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
            .andExpect(jsonPath("$.info").exists())
            .andDo(restDocument("update-project",
                pathParameters(
                    parameterWithName("projectId").description("Project identifier.")
                ),
                requestFields(
                    fieldWithPath("name").description("Name to update project."),
                    fieldWithPath("description").description("Description to update project")
                ),
                responseFields(
                    fieldWithPath("info").description("Message about action success.")
                )
                )
            );
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldReturnOwnerProject()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        when(this.queryService.findById(eq(projectId.id()), any(Architector.class)))
            .thenReturn(sampleProject(
                projectId,
                new AccessGrantedInfo(
                    List.of("spider@architector.ru"),
                    List.of("captain-america@architector.ru")
                )
            ));

        // expect
        this.mockMvc.perform(get("/api/projects/{projectId}", projectId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(projectId.id()))
            .andExpect(jsonPath("$.author").isString())
            .andExpect(jsonPath("$.createdDate").isString())
            .andExpect(jsonPath("$.updatedDate").isString())
            .andExpect(jsonPath("$.accessRights").isString())
            .andExpect(jsonPath("$.projectName").isString())
            .andExpect(jsonPath("$.description").isString())
            .andExpect(jsonPath("$.accessGrantedInfo").exists())
            .andExpect(jsonPath("$.accessGrantedInfo.readAccess").isArray())
            .andExpect(jsonPath("$.accessGrantedInfo.writeAccess").isArray())
            .andExpect(jsonPath("$.files").exists())
            .andDo(restDocument("get-project",
                pathParameters(
                    parameterWithName("projectId").description("Project identifier to search.")
                ),
                responseFields(
                    fieldWithPath("projectId").description("Project identifier."),
                    fieldWithPath("projectName").description("Project name."),
                    fieldWithPath("description").description("Project description"),
                    fieldWithPath("author").description("Project author."),
                    fieldWithPath("createdDate").description("Time of project creation in ISO-8601 format."),
                    fieldWithPath("updatedDate").description("Last project update time in ISO-8601 format."),
                    fieldWithPath("accessRights")
                        .description("Type of user access rights.\nAllowed values: [READ, WRITE, OWNER]."),
                    fieldWithPath("accessGrantedInfo")
                        .description("Info about users with read or write access to project.\nIs allowed only for " +
                                     "project's owner.")
                        .optional(),
                    fieldWithPath("accessGrantedInfo.readAccess")
                        .description("List of users with read access rights to project."),
                    fieldWithPath("accessGrantedInfo.writeAccess")
                        .description("List of users with write access rights to project."),
                    fieldWithPath("files").description("List of files in project.")
                ).andWithPrefix("files[*].",
                    fieldWithPath("fileId").description("File identifier."),
                    fieldWithPath("name").description("File name."),
                    fieldWithPath("schema").description("File ifc schema version."),
                    fieldWithPath("createdDate").description("Time of file creation in ISO-8601 format."),
                    fieldWithPath("updatedDate").description("Last file update time in ISO-8601 format.")
                )
                )
            );
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldReturnForeignProject()
        throws Exception
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        when(this.queryService.findById(eq(projectId.id()), any(Architector.class)))
            .thenReturn(sampleProject(projectId, null));

        // expect
        this.mockMvc.perform(get("/api/projects/{projectId}", projectId.id())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(projectId.id()))
            .andExpect(jsonPath("$.author").isString())
            .andExpect(jsonPath("$.createdDate").isString())
            .andExpect(jsonPath("$.updatedDate").isString())
            .andExpect(jsonPath("$.accessRights").isString())
            .andExpect(jsonPath("$.projectName").isString())
            .andExpect(jsonPath("$.description").isString())
            .andExpect(jsonPath("$.accessGrantedInfo").doesNotExist())
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
        String query = "tony";
        ProjectAccessRights accessRights = ProjectAccessRights.READ;
        when(this.queryService.findProjects(eq(query), eq(accessRights), any(Architector.class)))
            .thenReturn(List.of(
                sampleProject(
                    ProjectId.nextId(),
                    new AccessGrantedInfo(
                        List.of("spider@architector.ru"),
                        List.of("captain-america@architector.ru")
                    )
                )
            ));

        // expect
        this.mockMvc.perform(get("/api/projects?query={query}&accessType={accessType}", query, accessRights.name())
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projects").isArray())
            .andDo(restDocument("get-projects",
                requestParameters(
                    parameterWithName("query").description("Filter projects by name."),
                    parameterWithName("accessType").description("Filter projects by access rights.")
                ),
                responseFields().andWithPrefix(
                    "projects[*].",
                    fieldWithPath("projectId").description("Project identifier."),
                    fieldWithPath("projectName").description("Project name."),
                    fieldWithPath("description").description("Project description"),
                    fieldWithPath("author").description("Project author."),
                    fieldWithPath("createdDate").description("Time of project creation in ISO-8601 format."),
                    fieldWithPath("updatedDate").description("Last project update time in ISO-8601 format."),
                    fieldWithPath("accessRights")
                        .description("Type of user access rights.\nAllowed values: [READ, WRITE, OWNER]."),
                    fieldWithPath("accessGrantedInfo")
                        .description("Info about users with read or write access to project.\nIs allowed only for " +
                                     "project's owner.")
                        .optional(),
                    fieldWithPath("accessGrantedInfo.readAccess")
                        .description("List of users with read access rights to project."),
                    fieldWithPath("accessGrantedInfo.writeAccess")
                        .description("List of users with write access rights to project."),
                    fieldWithPath("files").description("List of files in project.")
                ).andWithPrefix("projects[*].files[*].",
                    fieldWithPath("fileId").description("File identifier."),
                    fieldWithPath("name").description("File name."),
                    fieldWithPath("schema").description("File ifc schema version."),
                    fieldWithPath("createdDate").description("Time of file creation in ISO-8601 format."),
                    fieldWithPath("updatedDate").description("Last file update time in ISO-8601 format.")
                )
                )
            );
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
            .andExpect(jsonPath("$.info").exists())
            .andDo(restDocument("grant-project-access-rights",
                pathParameters(
                    parameterWithName("projectId").description("Project identifier.")
                ),
                requestFields(
                    fieldWithPath("email").description("Email user to grant access rights to project."),
                    fieldWithPath("accessRights")
                        .description("Type of access rights to project. Allowed values: [READ, WRITE].")
                ),
                responseFields(
                    fieldWithPath("info").description("Message about action success.")
                )
                )
            );
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
            .andExpect(jsonPath("$.info").exists())
        .andDo(restDocument("take-away-project-access-rights",
        pathParameters(
            parameterWithName("projectId").description("Project identifier.")
        ),
        requestFields(
            fieldWithPath("email").description("Email user to take away access rights to project.")
        ),
        responseFields(
            fieldWithPath("info").description("Message about action success.")
        )
        )
    );
        verify(this.managementService, times(1))
            .takeAwayAccessRights(any(Architector.class), any(TakeAwayAccessRightsCommand.class));
    }

    private static ProjectData sampleProject(ProjectId projectId, AccessGrantedInfo accessGrantedInfo)
    {
        return ProjectData.builder()
            .projectId(projectId.id())
            .author("tony.stark@architector.ru")
            .name("Project")
            .description("Some description")
            .createdDate(LocalDateTime.now().minus(10, HOURS))
            .updatedDate(LocalDateTime.now())
            .accessRights(ProjectAccessRights.OWNER)
            .accessGrantedInfo(accessGrantedInfo)
            .files(List.of(ProjectObjectFactory.sampleFile()))
            .build();
    }

}
