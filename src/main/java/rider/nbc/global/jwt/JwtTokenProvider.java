package rider.nbc.global.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import rider.nbc.domain.user.entity.Role;
import rider.nbc.global.jwt.dto.TokenResponseDto;
import rider.nbc.global.jwt.jwtException.JwtAuthenticationException;
import rider.nbc.global.jwt.jwtException.JwtExceptionCode;

import javax.crypto.SecretKey;

@Component
@NoArgsConstructor
public class JwtTokenProvider {

	@Value("${jwt.secret-key}")
	private String secret;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	private SecretKey key;
	private final MacAlgorithm algorithm = Jwts.SIG.HS256;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateAccessToken(Long userId, String email, Role role) {
		return createToken(userId.toString(), email, role, accessTokenExpiration);
	}

	public String generateRefreshToken(Long userId, String email, Role role) {
		return createToken(userId.toString(), email, role, refreshTokenExpiration);
	}

	public TokenResponseDto generateTokenPair(Long userId, String email, Role role) {
		String accessToken = generateAccessToken(userId, email, role);
		String refreshToken = generateRefreshToken(userId, email, role);
		return new TokenResponseDto(accessToken, refreshToken);
	}

	private String createToken(String subject, String email, Role role, long expirationMs) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expirationMs);

		var builder = Jwts.builder()
				.subject(subject)
				.issuedAt(now)
				.expiration(expiry);

		if (email != null) builder.claim("email", email);
		if (role != null) builder.claim("role", role.name());

		return builder
				.signWith(key, algorithm)
				.compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token);
			return true;

		} catch (ExpiredJwtException e) {
			throw new JwtAuthenticationException(JwtExceptionCode.EXPIRED_TOKEN);

		} catch (SecurityException | MalformedJwtException e) {
			throw new JwtAuthenticationException(JwtExceptionCode.INVALID_SIGNATURE);

		} catch (JwtException | IllegalArgumentException e) {
			throw new JwtAuthenticationException(JwtExceptionCode.INVALID_TOKEN);
		}
	}

	private Claims parseClaims(String token) {
		try {
			return Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (ExpiredJwtException e) {
			return e.getClaims(); // 재발급용
		} catch (Exception e) {
			throw new JwtAuthenticationException(JwtExceptionCode.INVALID_TOKEN);
		}
	}

	public Long getAuthorId(String token) {
		return Long.parseLong(parseClaims(token).getSubject());
	}

	public String getEmail(String token) {
		return parseClaims(token).get("email", String.class);
	}

	public Role getRole(String token) {
		String roleStr = parseClaims(token).get("role", String.class);
		if (roleStr == null) {
			throw new JwtAuthenticationException(JwtExceptionCode.INVALID_TOKEN);
		}
		return Role.valueOf(roleStr);
	}

	public long getRemainingExpiration(String token) {
		return parseClaims(token).getExpiration().getTime() - System.currentTimeMillis();
	}

	public Duration getRefreshTokenDuration() {
		return Duration.ofMillis(refreshTokenExpiration);
	}

}
