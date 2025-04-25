package rider.nbc.global.jwt.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import rider.nbc.domain.user.entity.User;
import rider.nbc.global.jwt.JwtTokenProvider;
import rider.nbc.global.jwt.dto.TokenResponseDto;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public TokenResponseDto generateAndSaveTokenPair(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getNickname(), user.getRole()
        );
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(), user.getEmail(), user.getNickname(), user.getRole()
        );
        refreshTokenService.save(user.getId(), refreshToken, jwtTokenProvider.getRefreshTokenDuration());

        return new TokenResponseDto(accessToken, refreshToken);
    }


    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMinutes(60))
                .sameSite("Lax")
                .build();
    }
    public void clearTokenForUser(Long userId, HttpServletResponse response) {
        // 1. Redis refreshToken 삭제
        refreshTokenService.delete(userId);

        // 2. accessToken 쿠키 만료 설정
        ResponseCookie expiredCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
    }
}
