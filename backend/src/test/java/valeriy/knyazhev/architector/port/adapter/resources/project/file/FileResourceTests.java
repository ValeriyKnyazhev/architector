package valeriy.knyazhev.architector.port.adapter.resources.project.file;

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
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.request.FileFromUrlRequest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RunWith(SpringRunner.class)
@WebMvcTest(FileResource.class)
public class FileResourceTests {

    @MockBean
    private IFCFileReader fileReader;

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
    public void shouldAddFile()
            throws Exception {
        // given
        String fileUrl = "https://test.projects.ru/example.ifc";
        String createCommand = "{\"fileUrl\": \"" + fileUrl + "\"}";
        FileFromUrlRequest expectedCommand = new FileFromUrlRequest();
        expectedCommand.setSourceUrl(fileUrl);
        ProjectId projectId = ProjectId.nextId();
        Project project = sampleProject(projectId);
        File file = File.constructor().fileId(FileId.nextId()).construct();
        when(this.projectRepository.findByProjectId(any())).thenReturn(Optional.of(project));
        when(this.fileReader.readFromUrl(any())).thenReturn(file);

        // expect
        this.mockMvc.perform(post("/projects/{projectId}/files", projectId.id())
                .content(createCommand)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info").exists());
        verify(this.projectRepository, times(1)).save(project);
    }

}
