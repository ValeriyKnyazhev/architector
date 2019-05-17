package valeriy.knyazhev.architector.port.adapter.util;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class ArchitectorEmailResolver implements HandlerMethodArgumentResolver
{

    private static final String ARCHITECTOR_PARAMETER = "architector";

    @Override
    public boolean supportsParameter(MethodParameter parameter)
    {
        return parameter.getParameterType().equals(String.class)
               && ARCHITECTOR_PARAMETER.equals(parameter.getParameterName()
        );
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
    {
        return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }
}

