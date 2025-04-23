package rider.nbc.global.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rider.nbc.global.jwt.JwtAuthenticationFilter;
import rider.nbc.global.jwt.JwtTokenProvider;

/**
 * Spring Security 설정 클래스
 */
@Configuration
@EnableWebSecurity // 스프링시큐리티 기능 활성화
@AllArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * securityFilterChain 빈 정의
     *
     * @param http
     *         HttpSecurity 객체
     * @return SecurityFilterChain 객체
     * @throws Exception
     *         설정 중 오류 발생 시 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 모든 요청에 대해 보안 정책을 적용함 (securityMatcher 선택적)
                .securityMatcher((request -> true))

                // CSRF 보호 비활성화 (JWT 세션을 사용하지 않기 때문에 필요 없음)
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 사용 안함 (STATELESS)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/stores/**").hasRole("CEO")
                        .requestMatchers(HttpMethod.POST, "/api/v1/stores/**").hasRole("CEO") // 가게생성
                        .requestMatchers(HttpMethod.PUT, "/api/v1/stores/**").hasRole("CEO") // 가게수정
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/stores/**").hasRole("CEO") // 가게삭제
                        .requestMatchers(HttpMethod.GET, "/api/v1/stores/**").permitAll() // 가게 조회 모두 허용
                        .requestMatchers(HttpMethod.POST, "/api/v1/stores/*/menus/**").hasRole("CEO") // 메뉴생성
                        .requestMatchers(HttpMethod.PUT, "/api/v1/stores/*/menus/**").hasRole("CEO") // 메뉴수정
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/stores/*/menus/**").hasRole("CEO") // 메뉴삭제
                        .requestMatchers(HttpMethod.GET, "/api/v1/stores/*/menus/**").permitAll() // 가게 내 메뉴 조회 모두 허용
                        .requestMatchers(
                                "/api/v1/users/signup",         // 로그인, 회원가입, 재발급 등 인증 없이 접근 가능
                                "/api/v1/users/signin",
                                "/api/v1/users/reissue",
                                "/oauth2/**",        // 소셜 로그인 경로도 포함
                                "/v3/api-docs/**",  // 스웨거 관련 경로
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // JWT 인증 필터 등록
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                // 최종 SecurityFilterChain 반환
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }
}