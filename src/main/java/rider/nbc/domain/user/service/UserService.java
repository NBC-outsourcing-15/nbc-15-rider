package rider.nbc.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rider.nbc.domain.user.dto.LoginRequestDto;
import rider.nbc.domain.user.dto.ReissueRequestDto;
import rider.nbc.domain.user.dto.SignupRequestDto;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.entity.UserStatus;
import rider.nbc.domain.user.exception.UserException;
import rider.nbc.domain.user.exception.UserExceptionCode;
import rider.nbc.domain.user.repository.UserRepository;
import rider.nbc.global.jwt.JwtTokenProvider;
import rider.nbc.global.jwt.TokenResponseDto;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

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

        TokenResponseDto token = jwtTokenProvider.generateTokenPair(user.getId().toString());

        // Redis에 RefreshToken 저장
        redisTemplate.opsForValue().set(
                "refresh_token:" + user.getId(),
                token.getRefreshToken(),
                jwtTokenProvider.getRefreshTokenDuration()
        );

        return token;
    }

    public TokenResponseDto reissue(ReissueRequestDto dto) {
        String refreshToken = dto.getRefreshToken();

        // 1. 사용자 ID 추출
        String userId = jwtTokenProvider.getAuthorId(refreshToken);

        // 2. Redis 에 저장된 토큰 조회
        String redisKey = "refresh_token:" + userId;
        String savedToken = redisTemplate.opsForValue().get(redisKey);

        // 3. 비교
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new UserException(UserExceptionCode.TOKEN_NOT_MATCHED);
        }

        // 4. 새 토큰 발급
        TokenResponseDto newToken = jwtTokenProvider.generateTokenPair(userId);

        // 5. Redis 갱신
        redisTemplate.opsForValue().set(redisKey, newToken.getRefreshToken(), jwtTokenProvider.getRefreshTokenDuration()
        );

        return newToken;
    }

    public void logout(Long userId) {
        String redisKey = "refresh_token:" + userId;

        Boolean deleted = redisTemplate.delete(redisKey);

        if (Boolean.FALSE.equals(deleted)) {
            throw new UserException(UserExceptionCode.TOKEN_NOT_MATCHED);
        }
    }

    @Transactional
    public void withdraw(Long userId, String rawPassword) {
        User user = userRepository.findActiveByIdOrThrow(userId);

        user.validateIsActive();
        user.validatePassword(rawPassword, passwordEncoder);

        user.softDelete();
    }

}
