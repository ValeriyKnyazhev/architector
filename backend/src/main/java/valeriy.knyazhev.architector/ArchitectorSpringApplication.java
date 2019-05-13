package valeriy.knyazhev.architector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author valeriy.knyazhev@yandex.ru
 */
@EnableWebSecurity
@SpringBootApplication
public class ArchitectorSpringApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(ArchitectorSpringApplication.class, args);
    }

    @Configuration
    public class WebConfig implements WebMvcConfigurer
    {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry)
        {
            registry
                .addResourceHandler("/static/**")
                .addResourceLocations("classpath:public/static/");
        }

        @Override
        public void addViewControllers(ViewControllerRegistry registry)
        {
            registry.addViewController("/login").setViewName("login");
        }

    }

    @Configuration
    public class SecurityConfig extends WebSecurityConfigurerAdapter
    {

        @Override
        public void configure(AuthenticationManagerBuilder auth)
            throws
            Exception
        {
            //            auth.authenticationProvider(customAuthProvider);
            auth.inMemoryAuthentication()
                .withUser("user@architector.ru")
                .password(passwordEncoder().encode("pswd"))
                .roles("USER")
                .and()
                .withUser("admin@architector.ru")
                .password(passwordEncoder().encode("admin_pswd"))
                .roles("USER", "ADMIN");
        }

        @Override
        protected void configure(HttpSecurity http) throws
            Exception
        {
            http.csrf().disable();
            http.authorizeRequests().antMatchers("/login", "/logout").permitAll();
            http.authorizeRequests().antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')");
            http.authorizeRequests().anyRequest().authenticated();
            http.formLogin().loginPage("/login.html");
            http.logout().logoutUrl("/logout").invalidateHttpSession(true).deleteCookies("JSESSIONID");
            http.httpBasic();
        }


        @Bean
        public PasswordEncoder passwordEncoder()
        {
            return new BCryptPasswordEncoder();
        }
    }

}
