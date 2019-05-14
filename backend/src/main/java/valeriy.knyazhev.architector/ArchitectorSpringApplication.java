package valeriy.knyazhev.architector;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import valeriy.knyazhev.architector.port.adapter.resources.user.ArchitectorEmailResolver;

import java.util.List;

/**
 * @author valeriy.knyazhev@yandex.ru
 */
@SpringBootApplication
public class ArchitectorSpringApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(ArchitectorSpringApplication.class, args);
    }

    @Configuration
    @Controller
    @EnableWebMvc
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
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers)
        {
            argumentResolvers.add(new ArchitectorEmailResolver());
        }

        @GetMapping(value = {"/", "/projects/**", "/commits/**"}, produces = MediaType.TEXT_HTML_VALUE)
        public ModelAndView returnSpaBundle()
        {
            return new ModelAndView("index");
        }

    }

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig extends WebSecurityConfigurerAdapter
    {

        private final UserDetailsService userDetailsService;

        public SecurityConfig(@Qualifier("architectorDetailsService") UserDetailsService userDetailsService)
        {
            this.userDetailsService = userDetailsService;
        }

        @Bean
        public PasswordEncoder passwordEncoder()
        {
            return new BCryptPasswordEncoder();
        }

        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws
            Exception
        {
            auth.userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws
            Exception
        {
            http.csrf().disable();
            http.authorizeRequests().antMatchers(HttpMethod.POST, "/signup").anonymous();
            http.authorizeRequests().antMatchers("/login", "/logout").permitAll();
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

}
