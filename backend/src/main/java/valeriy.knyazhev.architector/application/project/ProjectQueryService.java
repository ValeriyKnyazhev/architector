package valeriy.knyazhev.architector.application.project;

import lombok.RequiredArgsConstructor;
import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Service
@RequiredArgsConstructor
public class ProjectQueryService
{

    private final ProjectRepository repository;

    @Nullable
    public Project findById(@Nonnull String qProjectId)
    {
        Args.notNull(qProjectId, "Project identifier is required.");
        ProjectId projectId = ProjectId.of(qProjectId);
        return this.repository.findByProjectId(projectId)
            .orElse(null);
    }

    @Nonnull
    public List<Project> findProjects(@Nullable String architector)
    {
        List<Project> allProjects = this.repository.findAll();
        if (architector != null)
        {
            return allProjects.stream()
                .filter(project -> architector.equals(project.author()))
                .collect(Collectors.toList());
        } else
        {
            return allProjects;
        }
    }

}
