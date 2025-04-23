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
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.menu.exception.MenuException;
import rider.nbc.domain.menu.exception.MenuExceptionCode;
import rider.nbc.domain.menu.repository.MenuRepository;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.fixture.OwnerFixture;
import rider.nbc.domain.store.fixture.StoreFixture;
import rider.nbc.domain.store.repository.StoreRepository;
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
		String userId = "1";
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
				User owner = defaultUser("CEO");
				Store store = StoreFixture.storeFrom(storeId, owner);
				Menu expectedMenu = requestDto.toEntity(store);

				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(owner);
				when(storeRepository.findById(storeId)).thenReturn(java.util.Optional.of(store));
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
				User owner = defaultUser("CEO");
				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(owner);
				when(storeRepository.findById(storeId)).thenReturn(java.util.Optional.empty());

				// When & Then
				assertThatThrownBy(() -> menuService.createMenu(userId, storeId, requestDto))
					.isInstanceOf(MenuException.class)
					.hasMessage(MenuExceptionCode.STORE_NOT_FOUND.getMessage());
			}
		}

		@Nested
		@DisplayName("가게 소유자가 아닌 사용자가 요청하면")
		class Context_with_non_owner_user {

			@Test
			@DisplayName("NOT_STORE_OWNER 예외를 던진다")
			void it_throws_not_store_owner_exception() {
				// Given
				User owner = defaultUser("CEO");
				User nonOwner = OwnerFixture.UserFrom(owner.getId() + 1, "USER");
				Store store = StoreFixture.storeFrom(storeId, owner);

				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(nonOwner);
				when(storeRepository.findById(storeId)).thenReturn(java.util.Optional.of(store));

				// When & Then
				assertThatThrownBy(() -> menuService.createMenu(userId, storeId, requestDto))
					.isInstanceOf(MenuException.class)
					.hasMessage(MenuExceptionCode.NOT_STORE_OWNER.getMessage());
			}
		}
	}
}