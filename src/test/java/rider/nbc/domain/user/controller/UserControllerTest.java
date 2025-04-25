package rider.nbc.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import rider.nbc.domain.user.dto.LoginRequestDto;
import rider.nbc.domain.user.dto.ReissueRequestDto;
import rider.nbc.domain.user.dto.SignupRequestDto;
import rider.nbc.domain.user.dto.WithdrawRequestDto;
import rider.nbc.domain.user.entity.Role;
import rider.nbc.domain.user.entity.SocialType;
import rider.nbc.domain.user.service.UserService;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.jwt.JwtTokenProvider;
import rider.nbc.global.jwt.dto.TokenResponseDto;

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

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

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

/*    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception {
        LoginRequestDto request = new LoginRequestDto();
        setField(request, "email", "user@email.com");
        setField(request, "password", "Password123!");

        when(userService.login(any())).thenReturn(new TokenResponseDto("access", "refresh"));

        mockMvc.perform(post("/api/v1/users/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));
    }*/

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
    void logout() throws Exception {
        AuthUser authUser = new AuthUser(1L, "user@email.com", "nickname", Role.ROLE_USER);

        mockMvc.perform(post("/api/v1/users/logout")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        authUser,
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                )
                .andExpect(status().isOk());

        verify(userService).logout(1L);
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void withdraw() throws Exception {
        WithdrawRequestDto request = new WithdrawRequestDto();
        setField(request, "password", "Password123!");

        AuthUser authUser = new AuthUser(1L, "user@email.com", "nickname", Role.ROLE_USER);

        mockMvc.perform(delete("/api/v1/users/withdraw")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        authUser,
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        verify(userService).withdraw(eq(1L), eq("Password123!"));
    }

}
