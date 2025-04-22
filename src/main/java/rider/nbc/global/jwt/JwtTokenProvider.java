package rider.nbc.global.jwt;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class JwtTokenProvider {

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	private Key key;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String generateAccessToken(String authorId) {
		return createToken(authorId, accessTokenExpiration);
	}

	public String generateRefreshToken(String authorId) {
		return createToken(authorId, refreshTokenExpiration);
	}

	public TokenResponseDto generateTokenPair(String authorId) {
		String accessToken = generateAccessToken(authorId);
		String refreshToken = generateRefreshToken(authorId);
		return new TokenResponseDto(accessToken, refreshToken);
	}

	private String createToken(String subject, long expireTimeMs) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expireTimeMs);

		return Jwts.builder()
			.setSubject(subject)
			.setIssuedAt(now)
			.setExpiration(expiry)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String getAuthorId(String token) {
		return Jwts.parser()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}

	public long getRemainingExpiration(String token) {
		Date expiration = Jwts.parser()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getExpiration();

		return expiration.getTime() - System.currentTimeMillis();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;

		} catch (ExpiredJwtException e) {
			// TODO
			throw new RuntimeException("TODO Implmented");
		} catch (UnsupportedJwtException e) {
			// TODO
			throw new RuntimeException("TODO Implmented");
		} catch (SecurityException | MalformedJwtException e) {
			// TODO
			throw new RuntimeException("TODO Implmented");
		} catch (JwtException | IllegalArgumentException e) {
			// TODO
			throw new RuntimeException("TODO Implmented");
		}
	}

	public Duration getRefreshTokenDuration() {
		return Duration.ofMillis(refreshTokenExpiration);
	}

}
