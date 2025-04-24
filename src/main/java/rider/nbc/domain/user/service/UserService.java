package rider.nbc.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rider.nbc.domain.user.dto.LoginRequestDto;
import rider.nbc.domain.user.dto.ReissueRequestDto;
import rider.nbc.domain.user.dto.SignupRequestDto;
import rider.nbc.domain.user.entity.Role;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.entity.UserStatus;
import rider.nbc.domain.user.exception.UserException;
import rider.nbc.domain.user.exception.UserExceptionCode;
import rider.nbc.domain.user.repository.UserRepository;
import rider.nbc.global.jwt.JwtTokenProvider;
import rider.nbc.global.jwt.dto.TokenResponseDto;
import rider.nbc.global.jwt.service.RefreshTokenService;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public void signup(SignupRequestDto dto) {
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        userRepository.checkEmailDuplicate(dto.getEmail());
        userRepository.checkNicknameDuplicate(dto.getNickname());

        User user = User.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .nickname(dto.getNickname())
                .phone(dto.getPhone())
                .role(dto.getRole())
                .status(UserStatus.ACTIVE)
                .point(0L)
                .socialType(dto.getSocialType())
                .build();

        userRepository.save(user);
    }

    public TokenResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmailOrThrow(dto.getEmail());
        user.validateIsActive(); // soft delete 확인
        user.validatePassword(dto.getPassword(), passwordEncoder);

        TokenResponseDto token = jwtTokenProvider.generateTokenPair(user.getId(), user.getEmail(),user.getNickname(), user.getRole());

        // Redis에 RefreshToken 저장
        refreshTokenService.save(user.getId(), token.getRefreshToken(), jwtTokenProvider.getRefreshTokenDuration());

        return token;
    }

    public TokenResponseDto reissue(ReissueRequestDto dto) {
        String refreshToken = dto.getRefreshToken();

        Long userId = jwtTokenProvider.getAuthorId(refreshToken);
        String email = jwtTokenProvider.getEmail(refreshToken);
        String nickname = jwtTokenProvider.getNickname(refreshToken);
        Role role = jwtTokenProvider.getRole(refreshToken);

        // 2. Redis 에 저장된 토큰 조회
        String savedToken = refreshTokenService.get(userId);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new UserException(UserExceptionCode.TOKEN_NOT_MATCHED);
        }

        // 4. 새 토큰 발급
        TokenResponseDto newToken = jwtTokenProvider.generateTokenPair(userId, email, nickname, role);

        // 5. Redis 갱신
        refreshTokenService.save(userId, newToken.getRefreshToken(), jwtTokenProvider.getRefreshTokenDuration());

        return newToken;
    }

    public void logout(Long userId) {
        if (!refreshTokenService.exists(userId)) {
            throw new UserException(UserExceptionCode.TOKEN_NOT_MATCHED);
        }
        refreshTokenService.delete(userId);
        SecurityContextHolder.clearContext();
    }


    @Transactional
    public void withdraw(Long userId, String rawPassword) {
        User user = userRepository.findActiveByIdOrThrow(userId);

        user.validateIsActive();
        user.validatePassword(rawPassword, passwordEncoder);
        user.softDelete();

        logout(userId);
    }

}
