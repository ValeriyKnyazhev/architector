package valeriy.knyazhev.architector.domain.model.project.commit;

import org.springframework.data.jpa.repository.JpaRepository;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public interface CommitRepository extends JpaRepository<Commit, Long> {

    @Nonnull
    public List<Commit> findByProjectIdOrderById(@Nonnull ProjectId projectId);

}