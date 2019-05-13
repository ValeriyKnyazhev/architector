package valeriy.knyazhev.architector.domain.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Valeriy Knyazhev
 */
public interface ArchitectorRepository extends JpaRepository<Architector, Long>
{
    public Optional<Architector> findByEmail(String email);
}
