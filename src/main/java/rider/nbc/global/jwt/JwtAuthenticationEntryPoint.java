package rider.nbc.global.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import rider.nbc.global.jwt.jwtException.JwtAuthenticationException;
import rider.nbc.global.jwt.jwtException.JwtExceptionCode;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 기본값: INVALID_TOKEN
        JwtExceptionCode errorCode = JwtExceptionCode.INVALID_TOKEN;

        if (authException instanceof JwtAuthenticationException jwtEx) {
            errorCode = jwtEx.getCode();
        }

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorResponse = String.format("""
                {
                  "success": %b,
                  "status": %d,
                  "message": "%s"
                }
                """, errorCode.isSuccess(), errorCode.getHttpStatus().value(), errorCode.getMessage());

        response.getWriter().write(errorResponse);
    }
}
