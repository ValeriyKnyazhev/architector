package valeriy.knyazhev.architector.domain.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author Valeriy Knyazhev
 */
public interface ArchitectorRepository extends JpaRepository<Architector, Long>
{

    @Nonnull
    public Optional<Architector> findByEmail(@Nonnull String email);

    @Nonnull
    public List<Architector> findByEmailIgnoreCaseContaining(@Nonnull String email);

}
