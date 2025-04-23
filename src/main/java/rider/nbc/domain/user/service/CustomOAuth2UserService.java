package rider.nbc.domain.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import rider.nbc.domain.user.dto.KakaoUserInfoResponse;
import rider.nbc.domain.user.entity.Role;
import rider.nbc.domain.user.entity.SocialType;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.entity.UserStatus;
import rider.nbc.domain.user.repository.UserRepository;
import rider.nbc.global.auth.AuthUser;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 카카오에서 사용자 정보 받아오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // DTO 에 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoUserInfoResponse kakaoUser = objectMapper.convertValue(attributes, KakaoUserInfoResponse.class);

        // 값 사용
        String email = kakaoUser.getEmail();
        String nickname = kakaoUser.getNickname();

        // 사용자 DB 조회 or 등록
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .nickname(nickname)
                        .role(Role.USER)
                        .status(UserStatus.ACTIVE)
                        .socialType(SocialType.KAKAO)
                        .build()));


        // 인증 객체 반환
        return new AuthUser(user.getId(), user.getEmail(),user.getNickname(), user.getRole());

    }
}
