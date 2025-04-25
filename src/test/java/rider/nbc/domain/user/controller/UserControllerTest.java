package rider.nbc.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import rider.nbc.domain.user.dto.LoginRequestDto;
import rider.nbc.domain.user.dto.ReissueRequestDto;
import rider.nbc.domain.user.dto.SignupRequestDto;
import rider.nbc.domain.user.dto.WithdrawRequestDto;
import rider.nbc.domain.user.entity.Role;
import rider.nbc.domain.user.entity.SocialType;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.entity.UserStatus;
import rider.nbc.domain.user.service.UserService;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.jwt.JwtTokenProvider;
import rider.nbc.global.jwt.dto.TokenResponseDto;
import rider.nbc.global.jwt.service.RefreshTokenService;
import rider.nbc.global.jwt.service.TokenService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;
    @MockBean private RefreshTokenService refreshTokenService;
    @MockBean private TokenService tokenService;

    @Test
    @DisplayName("회원가입 요청 성공")
    void signup() throws Exception {
        SignupRequestDto request = new SignupRequestDto();
        // 필드 설정
        setField(request, "email", "test@email.com");
        setField(request, "password", "Password123!");
        setField(request, "nickname", "닉네임");
        setField(request, "phone", "01012345678");
        setField(request, "role", Role.ROLE_USER);
        setField(request, "socialType", SocialType.NORMAL);

        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).signup(any(SignupRequestDto.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception {
        LoginRequestDto request = new LoginRequestDto();
        setField(request, "email", "user@email.com");
        setField(request, "password", "Password123!");

        User mockUser = User.builder()
                .id(1L)
                .email(request.getEmail())
                .password("encoded123!")
                .nickname("닉네임")
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .socialType(SocialType.NORMAL)
                .build();

        when(userService.login(any())).thenReturn(mockUser);
        when(tokenService.generateAndSaveTokenPair(any())).thenReturn(
                new TokenResponseDto("access", "refresh"));
        when(tokenService.createAccessTokenCookie(any()))
                .thenReturn(ResponseCookie.from("accessToken", "access").build());


        mockMvc.perform(post("/api/v1/users/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.accessToken").value("access"))
                .andExpect(jsonPath("$.result.refreshToken").value("refresh"));
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissue() throws Exception {
        ReissueRequestDto request = new ReissueRequestDto();
        setField(request, "refreshToken", "oldToken");

        when(userService.reissue(any())).thenReturn(new TokenResponseDto("newAccess", "newRefresh"));

        mockMvc.perform(post("/api/v1/users/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccess"));
    }


    @Test
    @DisplayName("로그아웃 성공")
    void logout() throws Exception {
        // given
        AuthUser mockAuthUser = new AuthUser(1L, "user@email.com", "닉네임", Role.ROLE_USER);

        // 인증 객체 설정
        TestingAuthenticationToken authentication =
                new TestingAuthenticationToken(mockAuthUser, null, mockAuthUser.getRole().name());
        authentication.setAuthenticated(true);

        // mock 설정
        doNothing().when(refreshTokenService).delete(mockAuthUser.getId());
        doNothing().when(tokenService).clearTokenForUser(eq(mockAuthUser.getId()), any());

        // when & then
        mockMvc.perform(post("/api/v1/users/logout")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃 완료"));
    }



    @Test
    @DisplayName("회원 탈퇴 성공")
    void withdraw() throws Exception {
        // given
        WithdrawRequestDto dto = new WithdrawRequestDto();
        ReflectionTestUtils.setField(dto, "password", "Password123!");

        AuthUser mockAuthUser = new AuthUser(1L, "user@email.com", "닉네임", Role.ROLE_USER);

        // 인증 객체 설정
        TestingAuthenticationToken authentication =
                new TestingAuthenticationToken(mockAuthUser, null, mockAuthUser.getRole().name());
        authentication.setAuthenticated(true);

        // mock 설정
        doNothing().when(userService).withdraw(mockAuthUser.getId(), dto.getPassword());
        doNothing().when(tokenService).clearTokenForUser(eq(mockAuthUser.getId()), any());

        // when & then
        mockMvc.perform(delete("/api/v1/users/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)) // ✅ 핵심!
                )
                .andExpect(status().isOk());
    }

}
