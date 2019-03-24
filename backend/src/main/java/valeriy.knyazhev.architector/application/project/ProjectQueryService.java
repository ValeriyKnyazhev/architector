package valeriy.knyazhev.architector.application.project;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.ProjectRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Service
@RequiredArgsConstructor
public class ProjectQueryService {


    private final ProjectRepository repository;

    @Nullable
    public Project findById(@Nonnull String qProjectId) {
        Args.notNull(qProjectId, "Project identifier is required.");
        ProjectId projectId = ProjectId.of(qProjectId);
        return this.repository.findByProjectId(projectId)
                .orElse(null);
    }

    @NonNull
    public List<Project> findAllProjects() {
        return this.repository.findAll();
    }

}
