package valeriy.knyazhev.architector.domain.model.project;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Nonnull
    public Optional<Project> findByProjectId(@Nonnull ProjectId projectId);

}