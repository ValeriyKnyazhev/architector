package valeriy.knyazhev.architector.port.adapter.resources.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import valeriy.knyazhev.architector.application.project.file.IFCFileReader;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ProjectResource.class)
public class ProjectResourceTests {

    @MockBean
    private IFCFileReader projectReader;

    @MockBean
    private ProjectRepository projectRepository;

    @Autowired
    private MockMvc mockMvc;

    private static Project sampleProject(ProjectId projectId) {
        return Project.constructor()
                .projectId(projectId)
                .construct();
    }

    @Test
    public void shouldCreateProject()
            throws Exception {
        // expect
        this.mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info").exists());
        verify(this.projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void shouldReturnProject()
            throws Exception {
        // given
        ProjectId projectId = ProjectId.nextId();
        when(this.projectRepository.findByProjectId(eq(projectId))).thenReturn(Optional.of(sampleProject(projectId)));

        // expect
        this.mockMvc.perform(get("/projects/{projectId}", projectId.id())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(projectId.id()))
                .andExpect(jsonPath("$.files").exists());
    }

}
