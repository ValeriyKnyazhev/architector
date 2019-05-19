package valeriy.knyazhev.architector.port.adapter.util;

import org.apache.commons.lang3.arch.Processor;
import org.apache.http.util.Args;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import valeriy.knyazhev.architector.application.user.ArchitectorNotFoundException;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.domain.model.user.ArchitectorRepository;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class ArchitectorResolver implements HandlerMethodArgumentResolver
{

    private static final String ARCHITECTOR_PARAMETER = "architector";

    private final ArchitectorRepository architectorRepository;

    public ArchitectorResolver(@Nonnull ArchitectorRepository architectorRepository)
    {
        this.architectorRepository = Args.notNull(architectorRepository, "Architector repository is required.");
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter)
    {
        return parameter.getParameterType().equals(Architector.class)
               && ARCHITECTOR_PARAMETER.equals(parameter.getParameterName()
        );
    }

    @Override
    public Architector resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
    {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getUsername();
        return this.architectorRepository.findByEmail(email)
            .orElseThrow(() -> new ArchitectorNotFoundException(email));
    }
}

