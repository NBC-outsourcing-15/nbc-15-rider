package rider.nbc.domain.menu.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static rider.nbc.domain.store.fixture.OwnerFixture.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import rider.nbc.domain.menu.dto.MenuCreateRequestDto;
import rider.nbc.domain.menu.dto.MenuUpdateRequestDto;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.menu.exception.MenuException;
import rider.nbc.domain.menu.exception.MenuExceptionCode;
import rider.nbc.domain.menu.fixture.MenuFixture;
import rider.nbc.domain.menu.repository.MenuRepository;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.exception.StoreException;
import rider.nbc.domain.store.exception.StoreExceptionCode;
import rider.nbc.domain.store.fixture.OwnerFixture;
import rider.nbc.domain.store.fixture.StoreFixture;
import rider.nbc.domain.store.repository.StoreRepository;
import rider.nbc.domain.user.entity.Role;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private MenuService menuService;

	@Nested
	@DisplayName("createMenu 메서드는")
	class Describe_createMenu {

		// 테스트에 사용할 공통 요청
		Long userId = 1L;
		Long storeId = 1L;
		MenuCreateRequestDto requestDto = MenuCreateRequestDto.builder()
			.name("Test Menu")
			.category("Test Category")
			.price(10000L)
			.menuPictureUrl("http://example.com/menu.jpg")
			.build();

		@Nested
		@DisplayName("유효한 사용자와 가게 소유자가 요청하면")
		class Context_with_valid_user_and_store_owner {

			@Test
			@DisplayName("메뉴를 생성하고 반환한다")
			void it_creates_and_returns_menu() {
				// Given
				User owner = defaultUser(Role.CEO);
				Store store = StoreFixture.storeFrom(storeId, owner);
				Menu expectedMenu = requestDto.toEntity(store);

				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(owner);
				when(storeRepository.findByIdOrElseThrow(storeId)).thenReturn(store);
				when(menuRepository.save(any(Menu.class))).thenReturn(expectedMenu);

				// When
				Menu result = menuService.createMenu(userId, storeId, requestDto);

				// Then
				assertThat(result).isNotNull();
				assertThat(result.getName()).isEqualTo("Test Menu");
				assertThat(result.getStore()).isEqualTo(store);
			}
		}

		@Nested
		@DisplayName("존재하지 않는 가게 ID가 주어지면")
		class Context_with_non_existent_store_id {

			@Test
			@DisplayName("STORE_NOT_FOUND 예외를 던진다")
			void it_throws_store_not_found_exception() {
				// Given
				User owner = defaultUser(Role.CEO);
				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(owner);
				when(storeRepository.findByIdOrElseThrow(storeId))
					.thenThrow(new StoreException(StoreExceptionCode.NOT_FOUND_STORE));

				// When & Then
				assertThatThrownBy(() -> menuService.createMenu(userId, storeId, requestDto))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_FOUND_STORE.getMessage());
			}
		}

		@Nested
		@DisplayName("가게 소유자가 아닌 사용자가 요청하면")
		class Context_with_non_owner_user {

			@Test
			@DisplayName("NOT_STORE_OWNER 예외를 던진다")
			void it_throws_not_store_owner_exception() {
				// Given
				User owner = defaultUser(Role.CEO);
				User nonOwner = OwnerFixture.UserFrom(owner.getId() + 1, Role.USER);
				Store store = StoreFixture.storeFrom(storeId, owner);

				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(nonOwner);
				when(storeRepository.findByIdOrElseThrow(storeId)).thenReturn(store);

				// When & Then
				assertThatThrownBy(() -> menuService.createMenu(userId, storeId, requestDto))
					.isInstanceOf(MenuException.class)
					.hasMessage(MenuExceptionCode.NOT_STORE_OWNER.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("updateMenu 메서드는")
	class Describe_updateMenu {

		// 테스트에 사용할 공통 요청
		Long userId = 1L;
		Long storeId = 1L;
		Long menuId = 1L;
		MenuUpdateRequestDto requestDto = MenuUpdateRequestDto.builder()
			.name("Updated Menu")
			.category("Updated Category")
			.price(15000L)
			.menuPictureUrl("http://example.com/updated-menu.jpg")
			.build();

		@Nested
		@DisplayName("유효한 사용자와 가게 소유자가 요청하면")
		class Context_with_valid_user_and_store_owner {

			@Test
			@DisplayName("메뉴를 수정하고 반환한다")
			void it_updates_and_returns_menu() {
				// Given
				User owner = defaultUser(Role.CEO);
				Store store = StoreFixture.storeFrom(storeId, owner);
				Menu menu = MenuFixture.menuFrom(menuId, store);

				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(owner);
				when(storeRepository.findStoreWithMenusByIdOrElseThrow(storeId)).thenReturn(store);
				when(menuRepository.findByIdOrElseThrow(menuId)).thenReturn(menu);

				// When
				Menu result = menuService.updateMenu(userId, storeId, menuId, requestDto);

				// Then
				assertThat(result).isNotNull();
				assertThat(result.getName()).isEqualTo("Updated Menu");
				assertThat(result.getCategory()).isEqualTo("Updated Category");
				assertThat(result.getPrice()).isEqualTo(15000L);
				assertThat(result.getMenuPictureUrl()).isEqualTo("http://example.com/updated-menu.jpg");
				assertThat(result.getStore()).isEqualTo(store);
			}
		}

		@Nested
		@DisplayName("존재하지 않는 가게 ID가 주어지면")
		class Context_with_non_existent_store_id {

			@Test
			@DisplayName("STORE_NOT_FOUND 예외를 던진다")
			void it_throws_store_not_found_exception() {
				// Given
				User owner = defaultUser(Role.CEO);
				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(owner);
				when(storeRepository.findStoreWithMenusByIdOrElseThrow(storeId))
					.thenThrow(new StoreException(StoreExceptionCode.NOT_FOUND_STORE));

				// When & Then
				assertThatThrownBy(() -> menuService.updateMenu(userId, storeId, menuId, requestDto))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_FOUND_STORE.getMessage());
			}
		}

		@Nested
		@DisplayName("가게 소유자가 아닌 사용자가 요청하면")
		class Context_with_non_owner_user {

			@Test
			@DisplayName("NOT_STORE_OWNER 예외를 던진다")
			void it_throws_not_store_owner_exception() {
				// Given
				User owner = defaultUser(Role.CEO);
				User nonOwner = OwnerFixture.UserFrom(owner.getId() + 1, Role.USER);
				Store store = StoreFixture.storeFrom(storeId, owner);

				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(nonOwner);
				when(storeRepository.findStoreWithMenusByIdOrElseThrow(storeId)).thenReturn(store);

				// When & Then
				assertThatThrownBy(() -> menuService.updateMenu(userId, storeId, menuId, requestDto))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_STORE_OWNER.getMessage());
			}
		}

		@Nested
		@DisplayName("존재하지 않는 메뉴 ID가 주어지면")
		class Context_with_non_existent_menu_id {

			@Test
			@DisplayName("MENU_NOT_FOUND 예외를 던진다")
			void it_throws_menu_not_found_exception() {
				// Given
				User owner = defaultUser(Role.CEO);
				Store store = StoreFixture.storeFrom(storeId, owner);

				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(owner);
				when(storeRepository.findStoreWithMenusByIdOrElseThrow(storeId)).thenReturn(store);
				when(menuRepository.findByIdOrElseThrow(menuId))
					.thenThrow(new MenuException(MenuExceptionCode.MENU_NOT_FOUND));

				// When & Then
				assertThatThrownBy(() -> menuService.updateMenu(userId, storeId, menuId, requestDto))
					.isInstanceOf(MenuException.class)
					.hasMessage(MenuExceptionCode.MENU_NOT_FOUND.getMessage());
			}
		}

		@Nested
		@DisplayName("메뉴가 해당 가게에 속하지 않으면")
		class Context_with_menu_not_belonging_to_store {

			@Test
			@DisplayName("MENU_NOT_FOUND 예외를 던진다")
			void it_throws_menu_not_found_exception() {
				// Given
				User owner = defaultUser(Role.CEO);
				Store store = StoreFixture.storeFrom(storeId, owner);
				Menu menu = MenuFixture.defaultMenu(menuId);

				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(owner);
				when(storeRepository.findStoreWithMenusByIdOrElseThrow(storeId)).thenReturn(store);
				when(menuRepository.findByIdOrElseThrow(menuId)).thenReturn(menu);

				// When & Then
				assertThatThrownBy(() -> menuService.updateMenu(userId, storeId, menuId, requestDto))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_CONTAINS_MENU.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("deleteMenu 메서드는")
	class Describe_deleteMenu {

		// 테스트에 사용할 공통 요청
		Long userId = 1L;
		Long storeId = 1L;
		Long menuId = 1L;

		@Nested
		@DisplayName("유효한 사용자와 가게 소유자가 요청하면")
		class Context_with_valid_user_and_store_owner {

			@Test
			@DisplayName("메뉴를 삭제한다")
			void it_deletes_menu() {
				// Given
				User owner = defaultUser(Role.CEO);
				Store store = StoreFixture.storeFrom(storeId, owner);
				Menu menu = MenuFixture.menuFrom(menuId, store);

				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(owner);
				when(storeRepository.findStoreWithMenusByIdOrElseThrow(storeId)).thenReturn(store);
				when(menuRepository.findByIdOrElseThrow(menuId)).thenReturn(menu);

				// When
				menuService.deleteMenu(userId, storeId, menuId);

				// Then
				verify(menuRepository).delete(menu);
			}
		}

		@Nested
		@DisplayName("존재하지 않는 가게 ID가 주어지면")
		class Context_with_non_existent_store_id {

			@Test
			@DisplayName("STORE_NOT_FOUND 예외를 던진다")
			void it_throws_store_not_found_exception() {
				// Given
				User owner = defaultUser(Role.CEO);
				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(owner);
				when(storeRepository.findStoreWithMenusByIdOrElseThrow(storeId))
					.thenThrow(new StoreException(StoreExceptionCode.NOT_FOUND_STORE));

				// When & Then
				assertThatThrownBy(() -> menuService.deleteMenu(userId, storeId, menuId))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_FOUND_STORE.getMessage());
			}
		}

		@Nested
		@DisplayName("가게 소유자가 아닌 사용자가 요청하면")
		class Context_with_non_owner_user {

			@Test
			@DisplayName("NOT_STORE_OWNER 예외를 던진다")
			void it_throws_not_store_owner_exception() {
				// Given
				User owner = defaultUser(Role.CEO);
				User nonOwner = OwnerFixture.UserFrom(owner.getId() + 1, Role.USER);
				Store store = StoreFixture.storeFrom(storeId, owner);

				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(nonOwner);
				when(storeRepository.findStoreWithMenusByIdOrElseThrow(storeId)).thenReturn(store);

				// When & Then
				assertThatThrownBy(() -> menuService.deleteMenu(userId, storeId, menuId))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_STORE_OWNER.getMessage());
			}
		}

		@Nested
		@DisplayName("존재하지 않는 메뉴 ID가 주어지면")
		class Context_with_non_existent_menu_id {

			@Test
			@DisplayName("MENU_NOT_FOUND 예외를 던진다")
			void it_throws_menu_not_found_exception() {
				// Given
				User owner = defaultUser(Role.CEO);
				Store store = StoreFixture.storeFrom(storeId, owner);

				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(owner);
				when(storeRepository.findStoreWithMenusByIdOrElseThrow(storeId)).thenReturn(store);
				when(menuRepository.findByIdOrElseThrow(menuId))
					.thenThrow(new MenuException(MenuExceptionCode.MENU_NOT_FOUND));

				// When & Then
				assertThatThrownBy(() -> menuService.deleteMenu(userId, storeId, menuId))
					.isInstanceOf(MenuException.class)
					.hasMessage(MenuExceptionCode.MENU_NOT_FOUND.getMessage());
			}
		}

		@Nested
		@DisplayName("메뉴가 해당 가게에 속하지 않으면")
		class Context_with_menu_not_belonging_to_store {

			@Test
			@DisplayName("NOT_CONTAINS_MENU 예외를 던진다")
			void it_throws_not_contains_menu_exception() {
				// Given
				User owner = defaultUser(Role.CEO);
				Store store = StoreFixture.storeFrom(storeId, owner);
				Menu menu = MenuFixture.defaultMenu(menuId);

				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(owner);
				when(storeRepository.findStoreWithMenusByIdOrElseThrow(storeId)).thenReturn(store);
				when(menuRepository.findByIdOrElseThrow(menuId)).thenReturn(menu);

				// When & Then
				assertThatThrownBy(() -> menuService.deleteMenu(userId, storeId, menuId))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_CONTAINS_MENU.getMessage());
			}
		}
	}
}
