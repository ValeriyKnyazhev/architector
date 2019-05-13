package valeriy.knyazhev.architector.domain.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Valeriy Knyazhev
 */
public interface RoleRepository extends JpaRepository<Role, Long>
{

}