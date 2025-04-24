package rider.nbc.domain.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import rider.nbc.domain.user.dto.LoginRequestDto;
import rider.nbc.domain.user.dto.ReissueRequestDto;
import rider.nbc.domain.user.dto.SignupRequestDto;
import rider.nbc.domain.user.entity.Role;
import rider.nbc.domain.user.entity.SocialType;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.entity.UserStatus;
import rider.nbc.domain.user.exception.UserException;
import rider.nbc.domain.user.repository.UserRepository;
import rider.nbc.global.jwt.JwtTokenProvider;
import rider.nbc.global.jwt.dto.TokenResponseDto;
import rider.nbc.global.jwt.service.RefreshTokenService;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        refreshTokenService = mock(RefreshTokenService.class);
        userService = new UserService(userRepository, passwordEncoder, jwtTokenProvider, refreshTokenService);
    }

    @Test
    void signup_정상동작() {
        SignupRequestDto dto = new SignupRequestDto();
        ReflectionTestUtils.setField(dto, "email", "user@email.com");
        ReflectionTestUtils.setField(dto, "password", "Password123!");
        ReflectionTestUtils.setField(dto, "nickname", "닉네임");
        ReflectionTestUtils.setField(dto, "phone", "01012345678");
        ReflectionTestUtils.setField(dto, "role", Role.ROLE_USER);
        ReflectionTestUtils.setField(dto, "socialType", SocialType.NORMAL);

        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPass");

        userService.signup(dto);

        verify(userRepository).checkEmailDuplicate(dto.getEmail());
        verify(userRepository).checkNicknameDuplicate(dto.getNickname());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_성공시_토큰반환() {
        LoginRequestDto dto = new LoginRequestDto();
        ReflectionTestUtils.setField(dto, "email", "user@email.com");
        ReflectionTestUtils.setField(dto, "password", "Password123!");

        User user = User.builder()
                .id(1L)
                .email(dto.getEmail())
                .password("encoded")
                .nickname("닉네임")
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .socialType(SocialType.NORMAL)
                .build();

        when(userRepository.findByEmailOrThrow(dto.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateTokenPair(any(), any(), any(), any()))
                .thenReturn(new TokenResponseDto("access-token", "refresh-token"));
        when(jwtTokenProvider.getRefreshTokenDuration()).thenReturn(Duration.ofDays(7));

        TokenResponseDto token = userService.login(dto);

        assertEquals("access-token", token.getAccessToken());
        assertEquals("refresh-token", token.getRefreshToken());
        verify(refreshTokenService).save(eq(user.getId()), eq("refresh-token"), any());
    }

    @Test
    void reissue_성공시_새로운_토큰발급() {
        ReissueRequestDto dto = new ReissueRequestDto();
        ReflectionTestUtils.setField(dto, "refreshToken", "oldRefreshToken");

        when(jwtTokenProvider.getAuthorId("oldRefreshToken")).thenReturn(1L);
        when(jwtTokenProvider.getEmail("oldRefreshToken")).thenReturn("user@email.com");
        when(jwtTokenProvider.getNickname("oldRefreshToken")).thenReturn("닉네임");
        when(jwtTokenProvider.getRole("oldRefreshToken")).thenReturn(Role.ROLE_USER);
        when(refreshTokenService.get(1L)).thenReturn("oldRefreshToken");
        when(jwtTokenProvider.generateTokenPair(any(), any(), any(), any()))
                .thenReturn(new TokenResponseDto("newAccess", "newRefresh"));
        when(jwtTokenProvider.getRefreshTokenDuration()).thenReturn(Duration.ofDays(7));

        TokenResponseDto result = userService.reissue(dto);

        assertEquals("newAccess", result.getAccessToken());
        assertEquals("newRefresh", result.getRefreshToken());
        verify(refreshTokenService).save(eq(1L), eq("newRefresh"), any());
    }

    @Test
    void logout_성공시_토큰삭제() {
        when(refreshTokenService.exists(1L)).thenReturn(true);
        userService.logout(1L);
        verify(refreshTokenService).delete(1L);
    }

    @Test
    void logout_토큰없을시_예외() {
        when(refreshTokenService.exists(1L)).thenReturn(false);
        assertThrows(UserException.class, () -> userService.logout(1L));
    }
}
