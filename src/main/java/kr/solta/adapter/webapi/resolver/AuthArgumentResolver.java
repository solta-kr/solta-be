package kr.solta.adapter.webapi.resolver;

import jakarta.servlet.http.HttpServletRequest;
import kr.solta.application.required.TokenProvider;
import kr.solta.application.required.dto.TokenPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class)
                && parameter.getParameterType().equals(AuthMember.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest를 찾을 수 없습니다.");
        }

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Authorization 헤더가 없거나 Bearer 형식이 아닙니다.");
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        TokenPayload payload = tokenProvider.extractPayload(token);

        return new AuthMember(payload.memberId());
    }
}
