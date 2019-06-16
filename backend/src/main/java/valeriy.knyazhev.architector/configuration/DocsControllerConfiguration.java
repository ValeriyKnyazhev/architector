package valeriy.knyazhev.architector.configuration;

import org.apache.http.util.Args;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import valeriy.knyazhev.architector.domain.model.user.ArchitectorRepository;
import valeriy.knyazhev.architector.port.adapter.util.ArchitectorResolver;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Valeriy Knyazhev
 */
@Configuration
@Controller
public class DocsControllerConfiguration implements WebMvcConfigurer
{

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/guides/**").addResourceLocations("classpath:/guides/");
    }

    @GetMapping("/docs")
    public String apiDocs()
    {
        return "/guides/html5/api.html";
    }

}