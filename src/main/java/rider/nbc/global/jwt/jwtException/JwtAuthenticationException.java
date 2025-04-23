package rider.nbc.global.jwt.jwtException;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {
    private final JwtExceptionCode code;

    public JwtAuthenticationException(JwtExceptionCode code) {
        super(code.getMessage());
        this.code = code;
    }

}
