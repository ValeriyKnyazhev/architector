package valeriy.knyazhev.architector.domain.model.project.commit;

import org.springframework.data.jpa.repository.JpaRepository;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public interface CommitRepository extends JpaRepository<Commit, Long>
{

    @Nonnull
    List<Commit> findByProjectIdOrderById(@Nonnull ProjectId projectId);

    @Nonnull
    List<Commit> findByProjectIdOrderByIdDesc(@Nonnull ProjectId projectId);

    @Nonnull
    Optional<Commit> findById(long id);

}