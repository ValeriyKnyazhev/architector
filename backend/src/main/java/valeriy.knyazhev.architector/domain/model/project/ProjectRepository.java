package valeriy.knyazhev.architector.domain.model.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public interface ProjectRepository extends JpaRepository<Project, Long>
{

    @Nonnull
    Optional<Project> findByProjectId(@Nonnull ProjectId projectId);

    @NonNull
    List<Project> findAll();

}