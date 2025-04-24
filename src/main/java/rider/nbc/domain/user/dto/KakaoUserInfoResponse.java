package rider.nbc.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoResponse {
    private Long id;
    private KakaoAccount kakao_account;

    // 커스텀 변환 메서드도 정의 가능
    public String getEmail() {
        return kakao_account != null ? kakao_account.getEmail() : null;
    }

    public String getNickname() {
        return kakao_account != null && kakao_account.getProfile() != null
                ? kakao_account.getProfile().getNickname() : null;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        private String email;
        private Profile profile;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
        private String nickname;
    }

}
