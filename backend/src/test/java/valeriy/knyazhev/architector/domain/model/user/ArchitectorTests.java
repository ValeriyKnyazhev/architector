package valeriy.knyazhev.architector.domain.model.user;

import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Valeriy Knyazhev
 */
public class ArchitectorTests
{

    @Test
    public void shouldSetArchitectorId() {
        // given
        Architector architector = new Architector();
        long id = 1L;

        // when
        architector.setId(id);

        // then
        assertThat(architector.id()).isEqualTo(id);
    }

    @Test
    public void shouldSetArchitectorEmail() {
        // given
        Architector architector = new Architector();
        String email = "tony.stark@architector.ru";

        // when
        architector.setEmail(email);

        // then
        assertThat(architector.email()).isEqualTo(email);
    }

    @Test
    public void shouldSetArchitectorPassword() {
        // given
        Architector architector = new Architector();
        String password = "pswd";

        // when
        architector.setPassword(password);

        // then
        assertThat(architector.password()).isEqualTo(password);
    }

    @Test
    public void shouldSetArchitectorRoles() {
        // given
        Architector architector = new Architector();
        Set<Role> roles = Stream.of("USER")
            .map(Role::new)
            .collect(Collectors.toSet());

        // when
        architector.setRoles(roles);

        // then
        assertThat(architector.roles()).isEqualTo(roles);
    }

    @Test
    public void shouldCheckArchitectorIsAdmin() {
        // given
        Architector architector = new Architector();
        Set<Role> roles = Stream.of("USER", "ADMIN")
            .map(Role::new)
            .collect(Collectors.toSet());
        architector.setRoles(roles);

        // expect
        assertThat(architector.isAdmin()).isTrue();
    }

    @Test
    public void shouldCheckArchitectorIsNotAdmin() {
        // given
        Architector architector = new Architector();
        Set<Role> roles = Stream.of("USER")
            .map(Role::new)
            .collect(Collectors.toSet());
        architector.setRoles(roles);

        // expect
        assertThat(architector.isAdmin()).isFalse();
    }

    @Test
    public void shouldCheckArchitectorsEquals()
    {
        // given
        Architector first = new Architector();
        Architector second = new Architector();
        long id = 1L;
        String email = "tony.stark@architector.ru";
        String password = "pswd";
        Set<Role> roles = Stream.of("USER")
            .map(Role::new)
            .collect(Collectors.toSet());
        long incorrectId = 2L;


        // when
        first.setId(id);
        first.setEmail(email);
        first.setPassword(password);
        first.setRoles(roles);
        second.setId(id);
        second.setEmail(email);
        second.setPassword(password);
        second.setRoles(roles);

        // then
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first).isNotEqualTo(null);

        // when
        first.setId(incorrectId);

        // then
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    public void shouldCheckArchitectorsHashcode()
    {
        // given
        Architector architector = new Architector();
        long id = 1L;
        String email = "tony.stark@architector.ru";
        String password = "pswd";
        Set<Role> roles = Stream.of("USER")
            .map(Role::new)
            .collect(Collectors.toSet());
        architector.setId(id);
        architector.setEmail(email);
        architector.setPassword(password);
        architector.setRoles(roles);

        // then
        assertThat(architector.hashCode()).isEqualTo(architector.hashCode());
    }

}