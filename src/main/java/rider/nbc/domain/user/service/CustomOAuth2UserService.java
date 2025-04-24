package rider.nbc.domain.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import rider.nbc.domain.user.dto.KakaoUserInfoResponse;
import rider.nbc.domain.user.dto.NaverUserInfoResponse;
import rider.nbc.domain.user.entity.Role;
import rider.nbc.domain.user.entity.SocialType;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.entity.UserStatus;
import rider.nbc.domain.user.exception.UserException;
import rider.nbc.domain.user.exception.UserExceptionCode;
import rider.nbc.domain.user.repository.UserRepository;
import rider.nbc.global.auth.AuthUser;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        User user;

        if ("kakao".equals(registrationId)) { // 카카오 로그인 실행 URI : http://localhost:8080/oauth2/authorization/kakao
            KakaoUserInfoResponse kakaoUser = objectMapper.convertValue(attributes, KakaoUserInfoResponse.class);
            String email = kakaoUser.getEmail();
            String nickname = kakaoUser.getNickname();

            userRepository.validateSocialJoinEmail(email, SocialType.KAKAO);

            user = userRepository.findByEmail(email).orElseGet(() ->
                    userRepository.save(User.builder()
                            .email(email)
                            .nickname(nickname)
                            .role(Role.USER)
                            .status(UserStatus.ACTIVE)
                            .socialType(SocialType.KAKAO)
                            .build()));

        } else if ("naver".equals(registrationId)) {  // 네이버 로그인 실행 URI : http://localhost:8080/oauth2/authorization/naver
            NaverUserInfoResponse naverUser = objectMapper.convertValue(attributes, NaverUserInfoResponse.class);
            String email = naverUser.getEmail();
            String nickname = naverUser.getNickname();
            String socialId = naverUser.getSocialId();

            userRepository.validateSocialJoinEmail(email, SocialType.NAVER);

            user = userRepository.findBySocialIdAndSocialType(socialId, SocialType.NAVER)
                    .orElseGet(() -> userRepository.save(User.builder()
                            .email(email)
                            .nickname(nickname)
                            .socialId(socialId)
                            .socialType(SocialType.NAVER)
                            .role(Role.USER)
                            .status(UserStatus.ACTIVE)
                            .build()));

        } else {
            throw new UserException(UserExceptionCode.UNSUPPORTED_SOCIAL_PROVIDER);
        }

        return new AuthUser(user.getId(), user.getEmail(), user.getNickname(), user.getRole());
    }
}
