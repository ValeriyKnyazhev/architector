package valeriy.knyazhev.architector.port.adapter.util;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.domain.model.user.ArchitectorRepository;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class ArchitectorResolver implements HandlerMethodArgumentResolver
{

    private static final String ARCHITECTOR_PARAMETER = "architector";

    @Override
    public boolean supportsParameter(MethodParameter parameter)
    {
        return parameter.getParameterType().equals(Architector.class)
               && ARCHITECTOR_PARAMETER.equals(parameter.getParameterName()
        );
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
    {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new Architector();
    }
}

