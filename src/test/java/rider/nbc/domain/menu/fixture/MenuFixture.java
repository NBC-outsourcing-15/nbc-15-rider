package rider.nbc.domain.menu.fixture;

import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.store.entity.Store;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 23.
 */
public class MenuFixture {
	public static Menu menuFrom(Long menuId, Store store) {
		Menu menu = Menu.builder()
			.id(menuId)
			.name("Original Menu")
			.category("Original Category")
			.price(10000L)
			.menuPictureUrl("http://example.com/original-menu.jpg")
			.store(store)
			.build();

		store.addMenu(menu);
		return menu;
	}

	public static Menu defaultMenu(Long menuId) {
		return Menu.builder()
			.id(menuId)
			.name("Original Menu")
			.category("Original Category")
			.price(10000L)
			.menuPictureUrl("http://example.com/original-menu.jpg")
			.build();
	}
}
