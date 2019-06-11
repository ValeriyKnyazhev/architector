package valeriy.knyazhev.architector.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import valeriy.knyazhev.architector.application.security.JwtConfigurer;
import valeriy.knyazhev.architector.application.security.JwtTokenProvider;
import valeriy.knyazhev.architector.application.user.ArchitectorDetailsService;

/**
 * @author Valeriy Knyazhev
 */
@Configuration
@Order(0)
@Profile("production")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{

    private final UserDetailsService detailsService;

    public SecurityConfiguration(ArchitectorDetailsService detailsService)
    {
        this.detailsService = detailsService;
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider()
    {
        return new JwtTokenProvider(this.detailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean()
        throws Exception
    {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth)
        throws Exception
    {
        auth.userDetailsService(this.detailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http)
        throws Exception
    {
        http.apply(new JwtConfigurer(jwtTokenProvider()));
        http.csrf().disable();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/signup").anonymous();
        http.authorizeRequests().antMatchers("/login", "/logout").permitAll();
        http.authorizeRequests().antMatchers("/api/token").permitAll();
        http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN");
        http.authorizeRequests().anyRequest().authenticated();
        http.formLogin()
            .defaultSuccessUrl("/projects", true)
            .permitAll();
        http.logout()
            .logoutUrl("/logout")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll();
        http.httpBasic();
    }

}