package rider.nbc.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rider.nbc.domain.user.dto.LoginRequestDto;
import rider.nbc.domain.user.dto.ReissueRequestDto;
import rider.nbc.domain.user.dto.SignupRequestDto;
import rider.nbc.domain.user.dto.WithdrawRequestDto;
import rider.nbc.domain.user.service.UserService;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.jwt.JwtTokenProvider;
import rider.nbc.global.jwt.dto.TokenResponseDto;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;

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

	@PostMapping("/reissue")
	public ResponseEntity<TokenResponseDto> reissue(@RequestBody ReissueRequestDto dto) {
		TokenResponseDto token = userService.reissue(dto);
		return ResponseEntity.ok(token);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthUser authUser) {
		userService.logout(authUser.getId());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/withdraw")
	public ResponseEntity<Void> withdraw(@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody WithdrawRequestDto dto) {
		userService.withdraw(authUser.getId(), dto.getPassword());
		return ResponseEntity.ok().build();
	}
}
