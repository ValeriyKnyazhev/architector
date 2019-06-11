package valeriy.knyazhev.architector.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Valeriy Knyazhev
 */
@Configuration
@Order(1)
@Profile({"default", "test"})
public class LocalSecurityConfiguration extends WebSecurityConfigurerAdapter
{

    @Override
    public void configure(AuthenticationManagerBuilder auth)
        throws Exception
    {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth.inMemoryAuthentication()
            .withUser(User.withUsername("tony.stark@architector.ru")
                .password("tony_best")
                .passwordEncoder(encoder::encode)
                .roles("ADMIN", "USER"));
    }

    @Override
    protected void configure(HttpSecurity http)
        throws Exception
    {
        http.csrf().disable();
        http.authorizeRequests().anyRequest().permitAll();
        http.httpBasic();
    }

}
