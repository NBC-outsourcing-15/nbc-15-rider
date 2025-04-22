package rider.nbc.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rider.nbc.domain.user.dto.LoginRequestDto;
import rider.nbc.domain.user.dto.SignupRequestDto;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.entity.UserStatus;
import rider.nbc.domain.user.repository.UserRepository;
import rider.nbc.global.jwt.JwtTokenProvider;
import rider.nbc.global.jwt.TokenResponseDto;

import java.time.Duration;

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
}
