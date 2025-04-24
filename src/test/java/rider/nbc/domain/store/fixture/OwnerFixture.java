package rider.nbc.domain.store.fixture;

import rider.nbc.domain.user.entity.Role;
import rider.nbc.domain.user.entity.User;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 23.
 */
public class OwnerFixture {

	public static User defaultUser(String role) {
		return User.builder()
			.id(1L)
			.email("test@example.com")
			.role(Role.ROLE_USER)
			.build();
	}

	public static User UserFrom(Long id, String role) {
		String email = "CEO".equals(role) ? "ceo@ceo.com" : "non-ceo@ceo.com";

		return User.builder()
			.id(id)
			.email(email)
			.role(Role.ROLE_CEO)
			.build();
	}
}
