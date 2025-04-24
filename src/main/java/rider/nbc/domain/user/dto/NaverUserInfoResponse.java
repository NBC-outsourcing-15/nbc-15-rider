package rider.nbc.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserInfoResponse {

    private Response response;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private String id;        // 네이버 유저 고유 ID
        private String email;     // 이메일 (선택적 동의)
        private String name;      // 이름 또는 닉네임
    }

    public String getSocialId() {
        return response != null ? response.getId() : null;
    }

    public String getEmail() {
        return response != null ? response.getEmail() : null;
    }

    public String getNickname() {
        return response != null ? response.getName() : null;
    }
}
