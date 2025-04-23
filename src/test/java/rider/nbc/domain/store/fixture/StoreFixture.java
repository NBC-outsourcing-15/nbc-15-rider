package rider.nbc.domain.store.fixture;

import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.user.entity.User;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 23.
 */
public class StoreFixture {
	public static Store storeFrom(Long storeId, User owner) {
		return Store.builder()
			.id(storeId)
			.name("Test Store")
			.owner(owner)
			.build();
	}
}
