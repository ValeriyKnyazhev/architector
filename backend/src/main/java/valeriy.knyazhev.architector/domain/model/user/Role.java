package valeriy.knyazhev.architector.domain.model.user;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Valeriy Knyazhev
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<Architector> architectors;

    public Role(String name)
    {
        this.name = name;
    }

    protected Role()
    {
        // empty
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Architector> getArchitectors() {
        return this.architectors;
    }

    public void setUsers(Set<Architector> architectors) {
        this.architectors = architectors;
    }

}