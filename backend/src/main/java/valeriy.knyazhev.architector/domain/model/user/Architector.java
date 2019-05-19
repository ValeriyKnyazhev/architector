package valeriy.knyazhev.architector.domain.model.user;

import lombok.EqualsAndHashCode;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    @ManyToMany
    private Set<Role> roles;

    public Long getId()
    {
        return this.id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getEmail()
    {
        return this.email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return this.password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Set<Role> getRoles()
    {
        return this.roles;
    }

    public void setRoles(Set<Role> roles)
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