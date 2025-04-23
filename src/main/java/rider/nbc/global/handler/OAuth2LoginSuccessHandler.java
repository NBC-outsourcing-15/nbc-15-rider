package rider.nbc.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.jwt.JwtTokenProvider;
import rider.nbc.global.jwt.dto.TokenResponseDto;
import rider.nbc.global.jwt.service.RefreshTokenService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            AuthUser authUser = (AuthUser) authentication.getPrincipal();
            log.info("OAuth 로그인 성공: {}", authUser.getEmail());

            String accessToken = jwtTokenProvider.generateAccessToken(authUser.getId(), authUser.getEmail(),authUser.getNickname() , authUser.getRole());
            String refreshToken = jwtTokenProvider.generateRefreshToken(authUser.getId(), authUser.getEmail(),authUser.getNickname() , authUser.getRole());

            refreshTokenService.save(authUser.getId(), refreshToken, jwtTokenProvider.getRefreshTokenDuration());

            TokenResponseDto tokenResponse = new TokenResponseDto(accessToken, refreshToken);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));

        } catch (Exception e) {
            log.error(" OAuth2 로그인 처리 중 에러 발생", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 실패");
        }
    }

}
