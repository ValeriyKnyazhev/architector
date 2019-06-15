package valeriy.knyazhev.architector.factory;

import valeriy.knyazhev.architector.domain.model.commit.*;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.domain.model.user.Role;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toSet;

/**
 * @author Valeriy Knyazhev
 */
public final class ArchitectorObjectFactory
{

    private ArchitectorObjectFactory()
    {
        // nop
    }

    public static Architector createArchitectorWithRoles(String email, Set<String> roles)
    {
        return buildArchitector(
            email,
            roles.stream().map(Role::new).collect(toSet())
            );
    }

    public static Architector createArchitector(String email)
    {
        return buildArchitector(email, Set.of());
    }

    private static Architector buildArchitector(String email, Set<Role> roles)
    {
        Architector architector = new Architector();
        architector.setId(1L);
        architector.setEmail(email);
        architector.setPassword("pswd");
        architector.setRoles(roles);
        return architector;
    }

}
