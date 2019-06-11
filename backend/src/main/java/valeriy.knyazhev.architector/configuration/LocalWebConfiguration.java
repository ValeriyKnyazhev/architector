package valeriy.knyazhev.architector.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.domain.model.user.Role;

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

    private static final Architector architector;

    static
    {
        architector = new Architector();
        architector.setId(1L);
        architector.setEmail("tony.stark@architector.ru");
        architector.setPassword("password");
        architector.setRoles(
            Stream.of("USER", "ADMIN")
                .map(Role::new)
                .collect(Collectors.toSet())
        );
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
                return architector;
            }
        });
    }

}