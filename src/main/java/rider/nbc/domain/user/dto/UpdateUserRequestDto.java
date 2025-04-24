package rider.nbc.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.user.entity.Role;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequestDto {
    @Email(message = "이메일 형식이 아닙니다")
    @NotBlank(message = "이메일은 필수 입력 값입니다")
    private String email;

    @NotBlank(message = "닉네임은 필수 입력 값입니다")
    private String nickname;

    @NotBlank(message = "핸드폰 번호는 필수 입력 값입니다")
    private String phone;
}
