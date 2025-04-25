package rider.nbc.domain.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rider.nbc.domain.user.dto.*;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.service.UserService;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.jwt.JwtTokenProvider;
import rider.nbc.global.jwt.dto.TokenResponseDto;
import rider.nbc.global.jwt.service.RefreshTokenService;
import rider.nbc.global.jwt.service.TokenService;
import rider.nbc.global.response.CommonResponse;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<CommonResponse<TokenResponseDto>> signin(@RequestBody LoginRequestDto requestDto) {
        User user = userService.login(requestDto);
        TokenResponseDto tokenDto = tokenService.generateAndSaveTokenPair(user);

        ResponseCookie cookie = tokenService.createAccessTokenCookie(tokenDto.getAccessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(CommonResponse.of(true, 200, "로그인 성공", tokenDto));
    }




    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<TokenResponseDto>> reissue(@RequestBody ReissueRequestDto reissueRequestDto) {
        TokenResponseDto tokenDto = userService.reissue(reissueRequestDto);

        ResponseCookie cookie = tokenService.createAccessTokenCookie(tokenDto.getAccessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(CommonResponse.of(true, 200, "토큰 재발급 완료", tokenDto));
    }


    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<String>> logout(@AuthenticationPrincipal AuthUser authUser,
                                                         HttpServletResponse response) {
        tokenService.clearTokenForUser(authUser.getId(), response);
        return ResponseEntity.ok(CommonResponse.of(true, 200, "로그아웃 완료", null));
    }



    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal AuthUser authUser,
                                         @Valid @RequestBody WithdrawRequestDto dto,
                                         HttpServletResponse response) {
        userService.withdraw(authUser.getId(), dto.getPassword());
        tokenService.clearTokenForUser(authUser.getId(), response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        UserResponseDto user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateUserResponseDto> updateUser(@PathVariable Long id,
                                                            @Valid @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        UpdateUserResponseDto user = userService.updateUser(id, updateUserRequestDto);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal AuthUser authUser,
                                               @Valid @RequestBody UpdatePasswordRequestDto request) {
        userService.updatePassword(authUser.getId(), request);
        return ResponseEntity.ok().build();
    }

}
