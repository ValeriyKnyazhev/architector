package valeriy.knyazhev.architector.application.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.application.project.command.*;
import valeriy.knyazhev.architector.application.user.ArchitectorNotFoundException;
import valeriy.knyazhev.architector.domain.model.AccessRightsNotFoundException;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.domain.model.user.ArchitectorRepository;
import valeriy.knyazhev.architector.domain.model.user.Role;

import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static valeriy.knyazhev.architector.factory.ArchitectorObjectFactory.createArchitector;
import static valeriy.knyazhev.architector.factory.ArchitectorObjectFactory.createArchitectorWithRoles;

/**
 * @author Valeriy Knyazhev
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProjectManagementService.class)
public class ProjectManagementServiceTests
{

    @Autowired
    private ProjectManagementService managementService;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private ArchitectorRepository architectorRepository;

    @Test
    public void shouldCreateProject()
    {
        // given
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor("author")
            .construct();
        CreateProjectCommand command = new CreateProjectCommand(
            project.name(), project.author(), project.description()
        );
        when(this.projectRepository.saveAndFlush(any(Project.class)))
            .thenReturn(project);

        // when
        ProjectId result = this.managementService.createProject(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(project.projectId());
    }

    @Test
    public void shouldUpdateProjectName()
    {
        // given
        Architector architector = createArchitector("tony.stark@architector.ru");
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor(architector.email())
            .construct();
        UpdateProjectDataCommand command = new UpdateProjectDataCommand(
            project.projectId().id(), "new", project.description(), project.author()
        );
        when(this.projectRepository.findByProjectId(project.projectId()))
            .thenReturn(Optional.of(project));

        // when
        boolean result = this.managementService.updateProjectData(architector, command);

        // then
        assertThat(result).isTrue();
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldUpdateProjectDescription()
    {
        // given
        Architector architector = createArchitector("tony.stark@architector.ru");
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor(architector.email())
            .construct();
        UpdateProjectDataCommand command = new UpdateProjectDataCommand(
            project.projectId().id(), project.name(), "new", project.author()
        );
        when(this.projectRepository.findByProjectId(project.projectId()))
            .thenReturn(Optional.of(project));

        // when
        boolean result = this.managementService.updateProjectData(architector, command);

        // then
        assertThat(result).isTrue();
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldNotUpdateProjectDataIfProjectNotFound()
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        Architector architector = createArchitector("tony.stark@architector.ru");
        UpdateProjectDataCommand command = new UpdateProjectDataCommand(
            projectId.id(), "new", "new", architector.email()
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> this.managementService.updateProjectData(architector, command))
        .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    public void shouldNotUpdateProjectDataIfForbidden()
    {
        // given
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor("other@architector.ru")
            .construct();
        Architector architector = createArchitector("tony.stark@architector.ru");
        UpdateProjectDataCommand command = new UpdateProjectDataCommand(
            project.projectId().id(), "new", "new", project.author()
        );
        when(this.projectRepository.findByProjectId(project.projectId()))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() -> this.managementService.updateProjectData(architector, command))
        .isExactlyInstanceOf(AccessRightsNotFoundException.class);
    }

    @Test
    public void shouldGrantReadAccessRightsIfOwner()
    {
        // given
        Architector architector = createArchitector("tony.stark@architector.ru");
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor(architector.email())
            .construct();
        ProjectId projectId = project.projectId();
        Architector userWithReadAccess = createArchitector("user@architector.ru");
        AddReadAccessRightsCommand command = new AddReadAccessRightsCommand(
            projectId.id(), userWithReadAccess.email()
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));
        when(this.architectorRepository.findByEmail(userWithReadAccess.email()))
            .thenReturn(Optional.of(userWithReadAccess));

        // when
        this.managementService.addReadAccessRights(architector, command);

        // then
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldGrantReadAccessRightsIfAdmin()
    {
        // given
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor("author")
            .construct();
        ProjectId projectId = project.projectId();
        Architector architector = createArchitectorWithRoles("tony.stark@architector.ru", Set.of("ADMIN"));
        Architector userWithReadAccess = createArchitector("user@architector.ru");
        AddReadAccessRightsCommand command = new AddReadAccessRightsCommand(
            projectId.id(), userWithReadAccess.email()
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));
        when(this.architectorRepository.findByEmail(userWithReadAccess.email()))
            .thenReturn(Optional.of(userWithReadAccess));

        // when
        this.managementService.addReadAccessRights(architector, command);

        // then
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldNotGrantReadAccessRightsIfProjectNotFound()
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        Architector architector = createArchitectorWithRoles("tony.stark@architector.ru", Set.of("ADMIN"));
        String readForUser = "user@architector.ru";
        AddReadAccessRightsCommand command = new AddReadAccessRightsCommand(
            projectId.id(), readForUser
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(()->this.managementService.addReadAccessRights(architector, command))
            .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    public void shouldNotGrantReadAccessRightsIfArchitectorNotFound()
    {
        // given
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor("author")
            .construct();
        ProjectId projectId = project.projectId();
        Architector architector = createArchitectorWithRoles("tony.stark@architector.ru", Set.of("ADMIN"));
        String readForUser = "user@architector.ru";
        AddReadAccessRightsCommand command = new AddReadAccessRightsCommand(
            projectId.id(), readForUser
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));
        when(this.architectorRepository.findByEmail(readForUser))
            .thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(()->this.managementService.addReadAccessRights(architector, command))
        .isExactlyInstanceOf(ArchitectorNotFoundException.class);
    }

    @Test
    public void shouldNotGrantReadAccessRightsIfForbidden()
    {
        // given
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor("author")
            .construct();
        ProjectId projectId = project.projectId();
        Architector architector = createArchitector("tony.stark@architector.ru");
        String readForUser = "user@architector.ru";
        AddReadAccessRightsCommand command = new AddReadAccessRightsCommand(
            projectId.id(), readForUser
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() ->this.managementService.addReadAccessRights(architector, command))
        .isExactlyInstanceOf(AccessRightsNotFoundException.class);
        verify(this.projectRepository, times(0)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldGrantWriteAccessRightsIfOwner()
    {
        // given
        Architector architector = createArchitector("tony.stark@architector.ru");
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor(architector.email())
            .construct();
        ProjectId projectId = project.projectId();
        Architector userWithWriteAccess = createArchitector("user@architector.ru");
        AddWriteAccessRightsCommand command = new AddWriteAccessRightsCommand(
            projectId.id(), userWithWriteAccess.email()
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));
        when(this.architectorRepository.findByEmail(userWithWriteAccess.email()))
            .thenReturn(Optional.of(userWithWriteAccess));

        // when
        this.managementService.addWriteAccessRights(architector, command);

        // then
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldGrantWriteAccessRightsIfAdmin()
    {
        // given
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor("author")
            .construct();
        ProjectId projectId = project.projectId();
        Architector architector = createArchitectorWithRoles("tony.stark@architector.ru", Set.of("ADMIN"));
        Architector userWithWriteAccess = createArchitector("user@architector.ru");
        AddWriteAccessRightsCommand command = new AddWriteAccessRightsCommand(
            projectId.id(), userWithWriteAccess.email()
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));
        when(this.architectorRepository.findByEmail(userWithWriteAccess.email()))
            .thenReturn(Optional.of(userWithWriteAccess));

        // when
        this.managementService.addWriteAccessRights(architector, command);

        // then
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldNotGrantWriteAccessRightsIfProjectNotFound()
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        Architector architector = createArchitectorWithRoles("tony.stark@architector.ru", Set.of("ADMIN"));
        String writeForUser = "user@architector.ru";
        AddWriteAccessRightsCommand command = new AddWriteAccessRightsCommand(
            projectId.id(), writeForUser
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(()->this.managementService.addWriteAccessRights(architector, command))
            .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    public void shouldNotGrantWriteAccessRightsIfArchitectorNotFound()
    {
        // given
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor("author")
            .construct();
        ProjectId projectId = project.projectId();
        Architector architector = createArchitectorWithRoles("tony.stark@architector.ru", Set.of("ADMIN"));
        String writeForUser = "user@architector.ru";
        AddWriteAccessRightsCommand command = new AddWriteAccessRightsCommand(
            projectId.id(), writeForUser
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));
        when(this.architectorRepository.findByEmail(writeForUser))
            .thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(()->this.managementService.addWriteAccessRights(architector, command))
            .isExactlyInstanceOf(ArchitectorNotFoundException.class);
    }

    @Test
    public void shouldNotGrantWriteAccessRightsIfForbidden()
    {
        // given
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor("author")
            .construct();
        ProjectId projectId = project.projectId();
        Architector architector = createArchitector("tony.stark@architector.ru");
        String writeForUser = "user@architector.ru";
        AddWriteAccessRightsCommand command = new AddWriteAccessRightsCommand(
            projectId.id(), writeForUser
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() ->this.managementService.addWriteAccessRights(architector, command))
            .isExactlyInstanceOf(AccessRightsNotFoundException.class);
        verify(this.projectRepository, times(0)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldTakeAwayAccessRightsIfOwner()
    {
        // given
        Architector architector = createArchitector("tony.stark@architector.ru");
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor(architector.email())
            .construct();
        ProjectId projectId = project.projectId();
        Architector userWithAccess = createArchitector("user@architector.ru");
        TakeAwayAccessRightsCommand command = new TakeAwayAccessRightsCommand(
            projectId.id(), userWithAccess.email()
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));
        when(this.architectorRepository.findByEmail(userWithAccess.email()))
            .thenReturn(Optional.of(userWithAccess));

        // when
        this.managementService.takeAwayAccessRights(architector, command);

        // then
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldTakeAwayAccessRightsIfAdmin()
    {
        // given
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor("author")
            .construct();
        ProjectId projectId = project.projectId();
        Architector architector = createArchitectorWithRoles("tony.stark@architector.ru", Set.of("ADMIN"));
        Architector userWithAccess = createArchitector("user@architector.ru");
        TakeAwayAccessRightsCommand command = new TakeAwayAccessRightsCommand(
            projectId.id(), userWithAccess.email()
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));
        when(this.architectorRepository.findByEmail(userWithAccess.email()))
            .thenReturn(Optional.of(userWithAccess));

        // when
        this.managementService.takeAwayAccessRights(architector, command);

        // then
        verify(this.projectRepository, times(1)).saveAndFlush(any(Project.class));
    }

    @Test
    public void shouldNotTakeAwayAccessRightsIfProjectNotFound()
    {
        // given
        ProjectId projectId = ProjectId.nextId();
        Architector architector = createArchitectorWithRoles("tony.stark@architector.ru", Set.of("ADMIN"));
        String userWithAccess = "user@architector.ru";
        TakeAwayAccessRightsCommand command = new TakeAwayAccessRightsCommand(
            projectId.id(), userWithAccess
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(()->this.managementService.takeAwayAccessRights(architector, command))
            .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    public void shouldNotTakeAwayAccessRightsIfArchitectorNotFound()
    {
        // given
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor("author")
            .construct();
        ProjectId projectId = project.projectId();
        Architector architector = createArchitectorWithRoles("tony.stark@architector.ru", Set.of("ADMIN"));
        String userWithAccess = "user@architector.ru";
        TakeAwayAccessRightsCommand command = new TakeAwayAccessRightsCommand(
            projectId.id(), userWithAccess
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));
        when(this.architectorRepository.findByEmail(userWithAccess))
            .thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(()->this.managementService.takeAwayAccessRights(architector, command))
            .isExactlyInstanceOf(ArchitectorNotFoundException.class);
    }

    @Test
    public void shouldNotTakeAwayAccessRightsIfForbidden()
    {
        // given
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("name")
            .withDescription("description")
            .withAuthor("author")
            .construct();
        ProjectId projectId = project.projectId();
        Architector architector = createArchitector("tony.stark@architector.ru");
        String userWithAccess = "user@architector.ru";
        TakeAwayAccessRightsCommand command = new TakeAwayAccessRightsCommand(
            projectId.id(), userWithAccess
        );
        when(this.projectRepository.findByProjectId(projectId))
            .thenReturn(Optional.of(project));

        // expect
        assertThatThrownBy(() ->this.managementService.takeAwayAccessRights(architector, command))
            .isExactlyInstanceOf(AccessRightsNotFoundException.class);
        verify(this.projectRepository, times(0)).saveAndFlush(any(Project.class));
    }

}