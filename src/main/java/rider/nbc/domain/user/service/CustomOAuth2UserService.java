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

        switch (registrationId) {
            case "kakao":
                // 카카오 로그인 처리
                KakaoUserInfoResponse kakaoUser = objectMapper.convertValue(attributes, KakaoUserInfoResponse.class);
                String kakaoEmail = kakaoUser.getEmail();
                String kakaoNickname = kakaoUser.getNickname();

                userRepository.validateSocialJoinEmail(kakaoEmail, SocialType.KAKAO);

                user = userRepository.findByEmail(kakaoEmail).orElseGet(() ->
                        userRepository.save(User.builder()
                                .email(kakaoEmail)
                                .nickname(kakaoNickname)
                                .role(Role.ROLE_USER)
                                .status(UserStatus.ACTIVE)
                                .socialType(SocialType.KAKAO)
                                .build()));
                break;

            case "naver":
                // 네이버 로그인 처리
                NaverUserInfoResponse naverUser = objectMapper.convertValue(attributes, NaverUserInfoResponse.class);
                String naverEmail = naverUser.getEmail();
                String naverNickname = naverUser.getNickname();
                String naverSocialId = naverUser.getSocialId();

                userRepository.validateSocialJoinEmail(naverEmail, SocialType.NAVER);

                user = userRepository.findBySocialIdAndSocialType(naverSocialId, SocialType.NAVER)
                        .orElseGet(() -> userRepository.save(User.builder()
                                .email(naverEmail)
                                .nickname(naverNickname)
                                .socialId(naverSocialId)
                                .socialType(SocialType.NAVER)
                                .role(Role.ROLE_USER)
                                .status(UserStatus.ACTIVE)
                                .build()));
                break;

            default:
                throw new UserException(UserExceptionCode.UNSUPPORTED_SOCIAL_PROVIDER);
        }

        return new AuthUser(user.getId(), user.getEmail(), user.getNickname(), user.getRole());
    }
}
