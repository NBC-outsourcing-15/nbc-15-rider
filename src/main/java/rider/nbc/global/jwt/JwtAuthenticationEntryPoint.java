package rider.nbc.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import rider.nbc.global.jwt.jwtException.JwtAuthenticationException;
import rider.nbc.global.jwt.jwtException.JwtErrorResponse;
import rider.nbc.global.jwt.jwtException.JwtExceptionCode;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 기본값: INVALID_TOKEN
        JwtExceptionCode errorCode = JwtExceptionCode.INVALID_TOKEN;

        if (authException instanceof JwtAuthenticationException jwtEx) {
            errorCode = jwtEx.getCode();
        }

        JwtErrorResponse errorResponse = new JwtErrorResponse(
                errorCode.isSuccess(),
                errorCode.getHttpStatus().value(),
                errorCode.getMessage()
        );

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
