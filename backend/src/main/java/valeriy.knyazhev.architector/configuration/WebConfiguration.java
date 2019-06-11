package valeriy.knyazhev.architector.configuration;

import org.apache.http.util.Args;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
@EnableWebMvc
@Profile("production")
public class WebConfiguration implements WebMvcConfigurer
{

    private final ArchitectorRepository architectorRepository;

    public WebConfiguration(@Nonnull ArchitectorRepository architectorRepository)
    {
        this.architectorRepository = Args.notNull(architectorRepository, "Architector repository is required.");
    }

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
        argumentResolvers.add(new ArchitectorResolver(this.architectorRepository));
    }

    @GetMapping(value = {"/", "/projects/**", "/commits/**"}, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView returnSpaBundle()
    {
        return new ModelAndView("index");
    }

}