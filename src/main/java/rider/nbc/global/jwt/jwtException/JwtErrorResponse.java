package rider.nbc.global.jwt.jwtException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtErrorResponse {
    private final boolean success;
    private final int status;
    private final String message;
}
