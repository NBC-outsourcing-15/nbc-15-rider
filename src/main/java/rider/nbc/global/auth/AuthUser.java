package rider.nbc.global.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rider.nbc.domain.user.entity.Role;

@Builder
@Getter
@Builder
@RequiredArgsConstructor
public class AuthUser implements OAuth2User {

	private final Long id;
	private final String email;
	private final String nickname;
	private final Role role;

	@Override
	public String getName() {
		return nickname != null ? nickname : "anonymous";
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public Map<String, Object> getAttributes() {
		return Collections.emptyMap();
	}
}
