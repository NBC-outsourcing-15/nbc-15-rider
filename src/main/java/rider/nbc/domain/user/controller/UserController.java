package rider.nbc.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rider.nbc.domain.user.dto.LoginRequestDto;
import rider.nbc.domain.user.dto.SignupRequestDto;
import rider.nbc.domain.user.service.UserService;
import rider.nbc.global.jwt.TokenResponseDto;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<TokenResponseDto> signin(@RequestBody LoginRequestDto requestDto) {
        TokenResponseDto token = userService.login(requestDto);
        return ResponseEntity.ok(token);
    }

}
