package valeriy.knyazhev.architector.application.user;

import org.apache.http.util.Args;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.domain.model.user.ArchitectorRepository;
import valeriy.knyazhev.architector.domain.model.user.Role;
import valeriy.knyazhev.architector.domain.model.user.RoleRepository;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Valeriy Knyazhev
 */
@Service
public class ArchitectorApplicationService
{

    private ArchitectorRepository architectorRepository;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    public ArchitectorApplicationService(@Nonnull ArchitectorRepository architectorRepository,
                                         @Nonnull RoleRepository roleRepository,
                                         @Nonnull PasswordEncoder passwordEncoder)
    {
        this.architectorRepository = Args.notNull(architectorRepository, "Architector repository is required.");
        this.roleRepository = Args.notNull(roleRepository, "Role repository is required.");
        this.passwordEncoder = Args.notNull(passwordEncoder, "Password encoder is required.");
    }

    @PostConstruct
    // FIXME add flyway and script to init db
    public void initDB()
    {
        Role userRole = new Role("USER");
        this.roleRepository.save(userRole);
        Role adminRole = new Role("ADMIN");
        this.roleRepository.save(adminRole);
        Architector user = new Architector();
        user.setEmail("user@architector.ru");
        user.setPassword(this.passwordEncoder.encode("pswd"));
        user.setRoles(Collections.singleton(userRole));
        this.architectorRepository.save(user);
        Architector admin = new Architector();
        admin.setEmail("admin@architector.ru");
        admin.setPassword(this.passwordEncoder.encode("admin_pswd"));
        admin.setRoles(Set.of(userRole, adminRole));
        this.architectorRepository.save(admin);
    }

    public Architector register(Architector architector)
    {
        this.architectorRepository.findByEmail(architector.getEmail())
            .ifPresent(user -> {
                    throw new ArchitectorAlreadyExistException(architector.getEmail());
                }
            );
        architector.setPassword(this.passwordEncoder.encode(architector.getPassword()));
        architector.setRoles(new HashSet<>(this.roleRepository.findAll()));
        return this.architectorRepository.save(architector);
    }

    public Architector findByEmail(String email)
    {
        return this.architectorRepository.findByEmail(email)
            .orElseThrow(() -> new ArchitectorNotFoundException(email));
    }

}
