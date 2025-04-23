package rider.nbc.domain.store.fixture;

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
			.role(role)
			.build();
	}
}
