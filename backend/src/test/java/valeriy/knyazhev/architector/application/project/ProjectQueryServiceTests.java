package valeriy.knyazhev.architector.application.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.domain.model.AccessRightsNotFoundException;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights.*;
import static valeriy.knyazhev.architector.factory.ArchitectorObjectFactory.createArchitector;
import static valeriy.knyazhev.architector.factory.ArchitectorObjectFactory.createArchitectorWithRoles;
import static valeriy.knyazhev.architector.factory.ProjectObjectFactory.projectWithFiles;

/**
 * @author Valeriy Knyazhev
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProjectQueryService.class)
public class ProjectQueryServiceTests
{

    @Autowired
    private ProjectQueryService queryService;

    @MockBean
    private ProjectRepository repository;

    @Test
    public void shouldFindProjectIfOwner()
    {
        // given
        Architector architector = createArchitector("tony.stark@architector.ru");
        Project project = projectWithFiles(architector.email());
        ProjectId projectId = project.projectId();
        when(this.repository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // when
        ProjectData result = this.queryService.findById(projectId.id(), architector);

        // then
        assertThat(result).isNotNull();
        assertThat(result.projectId()).isEqualTo(projectId.id());
        assertThat(result.name()).isEqualTo(project.name());
        assertThat(result.description()).isEqualTo(project.description());
        assertThat(result.author()).isEqualTo(project.author());
        assertThat(result.createdDate()).isEqualTo(project.createdDate());
        assertThat(result.updatedDate()).isEqualTo(project.updatedDate());
        assertThat(result.accessRights()).isEqualTo(OWNER);
        assertThat(result.accessGrantedInfo()).isNotNull();
        assertThat(result.files()).isNotEmpty();
    }

    @Test
    public void shouldFindProjectIfAdmin()
    {
        // given
        Architector architector = createArchitectorWithRoles("tony.stark@architector.ru", Set.of("ADMIN"));
        Project project = projectWithFiles("other@architector.ru");
        ProjectId projectId = project.projectId();
        when(this.repository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // when
        ProjectData result = this.queryService.findById(projectId.id(), architector);

        // then
        assertThat(result).isNotNull();
        assertThat(result.projectId()).isEqualTo(projectId.id());
        assertThat(result.name()).isEqualTo(project.name());
        assertThat(result.description()).isEqualTo(project.description());
        assertThat(result.author()).isEqualTo(project.author());
        assertThat(result.createdDate()).isEqualTo(project.createdDate());
        assertThat(result.updatedDate()).isEqualTo(project.updatedDate());
        assertThat(result.accessRights()).isEqualTo(WRITE);
        assertThat(result.accessGrantedInfo()).isNull();
        assertThat(result.files()).isNotEmpty();
    }

    @Test
    public void shouldFindProjectIfHasWriteAccess()
    {
        // given
        Architector architector = createArchitector("tony.stark@architector.ru");
        Project project = projectWithFiles("other@architector.ru");
        ProjectId projectId = project.projectId();
        project.addWriteAccessRights(architector);
        when(this.repository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // when
        ProjectData result = this.queryService.findById(projectId.id(), architector);

        // then
        assertThat(result).isNotNull();
        assertThat(result.projectId()).isEqualTo(projectId.id());
        assertThat(result.name()).isEqualTo(project.name());
        assertThat(result.description()).isEqualTo(project.description());
        assertThat(result.author()).isEqualTo(project.author());
        assertThat(result.createdDate()).isEqualTo(project.createdDate());
        assertThat(result.updatedDate()).isEqualTo(project.updatedDate());
        assertThat(result.accessRights()).isEqualTo(WRITE);
        assertThat(result.accessGrantedInfo()).isNull();
        assertThat(result.files()).isNotEmpty();
    }

    @Test
    public void shouldFindProjectIfHasReadAccess()
    {
        // given
        Architector architector = createArchitector("tony.stark@architector.ru");
        Project project = projectWithFiles("other@architector.ru");
        ProjectId projectId = project.projectId();
        project.addReadAccessRights(architector);
        when(this.repository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // when
        ProjectData result = this.queryService.findById(projectId.id(), architector);

        // then
        assertThat(result).isNotNull();
        assertThat(result.projectId()).isEqualTo(projectId.id());
        assertThat(result.name()).isEqualTo(project.name());
        assertThat(result.description()).isEqualTo(project.description());
        assertThat(result.author()).isEqualTo(project.author());
        assertThat(result.createdDate()).isEqualTo(project.createdDate());
        assertThat(result.updatedDate()).isEqualTo(project.updatedDate());
        assertThat(result.accessRights()).isEqualTo(READ);
        assertThat(result.accessGrantedInfo()).isNull();
        assertThat(result.files()).isNotEmpty();
    }

    @Test
    public void shouldFindNullProjectIfNotFound()
    {
        // given
        Architector architector = createArchitectorWithRoles("tony.stark@architector.ru", Set.of("ADMIN"));
        ProjectId projectId = ProjectId.nextId();
        when(this.repository.findByProjectId(projectId))
            .thenReturn(Optional.empty());

        // when
        ProjectData result = this.queryService.findById(projectId.id(), architector);

        // then
        assertThat(result).isNull();
    }

    @Test
    public void shouldNotFindProjectIfHasNotAccess()
    {
        // given
        Architector architector = createArchitector("tony.stark@architector.ru");
        Project project = projectWithFiles("other@architector.ru");
        ProjectId projectId = project.projectId();
        when(this.repository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() -> this.queryService.findById(projectId.id(), architector))
            .isExactlyInstanceOf(AccessRightsNotFoundException.class);
    }

    @Test
    public void shouldFindProjectsIfOwner()
    {
        // given
        Architector architector = createArchitector("tony.stark@architector.ru");
        Project project = projectWithFiles(architector.email());
        when(this.repository.findAll()).thenReturn(List.of(project));

        // when
        List<ProjectData> result = this.queryService.findProjects(architector);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    public void shouldFindProjectsIfAdmin()
    {
        // given
        Architector architector = createArchitectorWithRoles("tony.stark@architector.ru", Set.of("ADMIN"));
        Project project = projectWithFiles("other@architector.ru");
        when(this.repository.findAll()).thenReturn(List.of(project));

        // when
        List<ProjectData> result = this.queryService.findProjects(architector);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    public void shouldFindProjectsIfHasWriteAccess()
    {
        // given
        Architector architector = createArchitector("tony.stark@architector.ru");
        Project project = projectWithFiles("other@architector.ru");
        project.addWriteAccessRights(architector);
        when(this.repository.findAll()).thenReturn(List.of(project));

        // when
        List<ProjectData> result = this.queryService.findProjects(architector);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    public void shouldFindProjectsIfHasReadAccess()
    {
        // given
        Architector architector = createArchitector("tony.stark@architector.ru");
        Project project = projectWithFiles("other@architector.ru");
        project.addReadAccessRights(architector);
        when(this.repository.findAll()).thenReturn(List.of(project));

        // when
        List<ProjectData> result = this.queryService.findProjects(architector);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    public void shouldNotFindProjectsIfHasNotAccess()
    {
        // given
        Architector architector = createArchitector("tony.stark@architector.ru");
        Project project = projectWithFiles("other@architector.ru");
        when(this.repository.findAll()).thenReturn(List.of(project));

        // when
        List<ProjectData> result = this.queryService.findProjects(architector);

        // then
        assertThat(result).hasSize(0);
    }

}