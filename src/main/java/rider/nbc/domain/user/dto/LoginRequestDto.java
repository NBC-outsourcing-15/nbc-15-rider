package rider.nbc.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    @Email(message = "이메일 형식이 아닙니다")
    @NotBlank(message = "이메일은 필수 입력 값입니다")
    private String email;

    @NotBlank(message = "비밀번호를 적어주세요")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+={\\[\\]}:;\"'<,>.?/])[\\S]{8,}$",
            message = "비밀번호에는 대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함되어야 합니다."
    )
    private String password;
}
