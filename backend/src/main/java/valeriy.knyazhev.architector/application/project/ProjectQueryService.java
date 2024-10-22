package valeriy.knyazhev.architector.application.project;

import lombok.RequiredArgsConstructor;
import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.AccessRightsNotFoundException;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights;
import valeriy.knyazhev.architector.domain.model.user.Architector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights.OWNER;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Service
@RequiredArgsConstructor
public class ProjectQueryService
{

    private final ProjectRepository repository;

    @Nullable
    public ProjectData findById(@Nonnull String qProjectId, @Nonnull Architector architector)
    {
        Args.notNull(qProjectId, "Project identifier is required.");
        ProjectId projectId = ProjectId.of(qProjectId);
        Project project = this.repository.findByProjectId(projectId)
            .orElse(null);
        if (project == null)
        {
            return null;
        }
        if (!project.canBeRead(architector))
        {
            throw new AccessRightsNotFoundException();
        }
        return buildProjectData(project, architector);
    }

    @Nonnull
    public List<ProjectData> findProjects(@Nonnull String query,
                                          @Nonnull ProjectAccessRights accessType,
                                          @Nonnull Architector architector)
    {
        Stream<Project> projects = this.repository.findAll().stream()
            .filter(project -> project.name().contains(query));
        switch (accessType)
        {
            case READ:
            {
                projects = projects.filter(project -> project.canBeRead(architector));
                break;
            }
            case WRITE:
            {
                projects = projects.filter(project -> project.canBeUpdated(architector));
                break;
            }
            case OWNER:
            {
                projects = projects.filter(project -> project.author().equals(architector.email()));
                break;
            }
            default:
            {
            }
        }
        return projects.map(project -> buildProjectData(project, architector))
            .collect(toList());
    }

    @Nonnull
    private static ProjectData buildProjectData(@Nonnull Project project, @Nonnull Architector architector)
    {
        ProjectAccessRights accessRights = project.accessRights(architector);
        return ProjectData.builder()
            .projectId(project.projectId().id())
            .author(project.author())
            .name(project.name())
            .description(project.description())
            .accessRights(accessRights)
            .accessGrantedInfo(accessGranted(project, accessRights))
            .createdDate(project.createdDate())
            .updatedDate(project.updatedDate())
            .files(project.files())
            .build();
    }

    @Nullable
    private static AccessGrantedInfo accessGranted(@Nonnull Project project,
                                                   @Nonnull ProjectAccessRights accessRights)
    {
        if (accessRights != OWNER)
        {
            return null;
        } else
        {
            return new AccessGrantedInfo(
                project.readAccessRights().stream()
                    .map(Architector::email)
                    .collect(Collectors.toUnmodifiableList()),
                project.writeAccessRights().stream()
                    .map(Architector::email)
                    .collect(Collectors.toUnmodifiableList())
            );
        }
    }

}
