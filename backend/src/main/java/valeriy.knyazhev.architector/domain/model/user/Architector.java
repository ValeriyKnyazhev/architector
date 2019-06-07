package valeriy.knyazhev.architector.domain.model.user;

import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.Set;

/**
 * @author Valeriy Knyazhev
 */
@Entity
@EqualsAndHashCode
@Table(name = "architectors")
public class Architector
{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "architectorIdGenerator")
    @SequenceGenerator(name = "architectorIdGenerator", sequenceName = "architector_id_seq", allocationSize = 1)
    private Long id;

    @Nonnull
    private String email;

    @Nonnull
    private String password;

    @JoinTable(
        name = "architector_role_relations",
        joinColumns = @JoinColumn(name = "architector_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ManyToMany
    @Nonnull
    private Set<Role> roles;

    public Long id()
    {
        return this.id;
    }

    @Nonnull
    public String email()
    {
        return this.email;
    }

    @Nonnull
    public String password()
    {
        return this.password;
    }

    @Nonnull
    public Set<Role> roles()
    {
        return this.roles;
    }

    public void setId(Long id)
    {
        this.id = id;
    }


    public void setEmail(@Nonnull String email)
    {
        this.email = email;
    }

    public void setPassword(@Nonnull String password)
    {
        this.password = password;
    }

    public void setRoles(@Nonnull Set<Role> roles)
    {
        this.roles = roles;
    }

    public boolean isAdmin()
    {
        return this.roles.stream()
            .map(Role::getName)
            .anyMatch("ADMIN"::equals);

    }
}