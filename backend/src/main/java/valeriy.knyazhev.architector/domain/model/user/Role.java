package valeriy.knyazhev.architector.domain.model.user;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Valeriy Knyazhev
 */
@Entity
@Table(name = "architector_roles")
public class Role
{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleIdGenerator")
    @SequenceGenerator(name = "roleIdGenerator", sequenceName = "architector_role_id_seq", allocationSize = 1)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
        name = "architector_role_relations",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "architector_id")
    )
    private Set<Architector> architectors;

    public Role(String name)
    {
        this.name = name;
    }

    protected Role()
    {
        // empty
    }

    public Long getId()
    {
        return this.id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Set<Architector> getArchitectors()
    {
        return this.architectors;
    }

    public void setUsers(Set<Architector> architectors)
    {
        this.architectors = architectors;
    }

}