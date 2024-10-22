package valeriy.knyazhev.architector.application.user;

import org.apache.http.util.Args;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.domain.model.user.ArchitectorRepository;
import valeriy.knyazhev.architector.domain.model.user.Role;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Valeriy Knyazhev
 */
@Service
public class ArchitectorDetailsService implements UserDetailsService
{
    private ArchitectorRepository architectorRepository;

    public ArchitectorDetailsService(@Nonnull ArchitectorRepository architectorRepository)
    {
        this.architectorRepository = Args.notNull(architectorRepository, "Architector repository is required.");
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
    {
        Architector architector = this.architectorRepository.findByEmail(email)
            .orElseThrow(() -> new ArchitectorNotFoundException(email));
        Set<GrantedAuthority> grantedAuthorities = architector.roles().stream()
            .map(Role::getName)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
        return new User(
            architector.email(),
            architector.password(),
            grantedAuthorities
        );
    }
}