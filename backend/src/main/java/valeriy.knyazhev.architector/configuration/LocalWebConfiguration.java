package valeriy.knyazhev.architector.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.domain.model.user.Role;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Valeriy Knyazhev
 */
@Configuration
@Controller
@EnableWebMvc
@Profile({"default", "test"})
public class LocalWebConfiguration implements WebMvcConfigurer
{

    private static Architector createArchitector(@Nonnull String email,
                                                 @Nonnull String password,
                                                 @Nonnull Collection<? extends GrantedAuthority> authorities)
    {
        Architector architector = new Architector();
        architector.setId(1L);
        architector.setEmail(email);
        architector.setPassword(password);
        architector.setRoles(
            authorities.stream()
                .map(t -> (GrantedAuthority) t)
                .map(GrantedAuthority::getAuthority)
                .map(Role::new)
                .collect(Collectors.toSet())
        );
        return architector;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers)
    {
        argumentResolvers.add(new HandlerMethodArgumentResolver()
        {
            @Override public boolean supportsParameter(MethodParameter parameter)
            {
                return parameter.getParameterType().equals(Architector.class);
            }

            @Override public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                    NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
                throws Exception
            {
                UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
                return createArchitector(user.getUsername(), user.getPassword(), user.getAuthorities());
            }
        });
    }

}