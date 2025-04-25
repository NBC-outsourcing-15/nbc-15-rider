package rider.nbc.domain.store.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static rider.nbc.domain.store.fixture.OwnerFixture.*;

import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import rider.nbc.domain.store.dto.StoreCreateRequestDto;
import rider.nbc.domain.store.dto.StoreUpdateRequestDto;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.entity.StoreStatus;
import rider.nbc.domain.store.exception.StoreException;
import rider.nbc.domain.store.exception.StoreExceptionCode;
import rider.nbc.domain.store.fixture.StoreFixture;
import rider.nbc.domain.store.repository.StoreRepository;
import rider.nbc.domain.user.entity.Role;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private StoreService storeService;

	@Nested
	@DisplayName("createStore 메서드는")
	class Describe_createStore {

		// 테스트에 사용할 공통 요청
		Long ownerId = 1L;
		StoreCreateRequestDto requestDto = StoreCreateRequestDto.builder()
			.name("Test Store")
			.category("Test Category")
			.city("Test City")
			.district("Test District")
			.detailedAddress("Test Detailed Address")
			.openTime(LocalTime.of(9, 0))
			.closeTime(LocalTime.of(18, 0))
			.build();

		@Nested
		@DisplayName("유효한 사용자와 요청이 주어지면")
		class Context_with_valid_user_and_request {

			@Test
			@DisplayName("가게를 생성하고 반환한다")
			void it_creates_and_returns_store() {
				// Given
				User ceoUser = defaultUser(Role.ROLE_CEO);

				Store expectedStore = requestDto.toEntity(ceoUser);

				when(userRepository.findByOwnerIdOrThrow(anyLong())).thenReturn(ceoUser);
				when(storeRepository.countByOwner(ceoUser)).thenReturn(0L);
				when(storeRepository.save(any(Store.class))).thenReturn(expectedStore);

				// When
				Store result = storeService.createStore(ownerId, requestDto);

				// Then
				assertThat(result).isNotNull();
				assertThat(result.getName()).isEqualTo("Test Store");
				assertThat(result.getOwner()).isEqualTo(ceoUser);
			}
		}

		@Nested
		@DisplayName("존재하지 않는 사용자 ID가 주어지면")
		class Context_with_non_existent_user_id {

			@Test
			@DisplayName("OWNER_NOT_FOUND 예외를 던진다")
			void it_throws_owner_not_found_exception() {
				// Given
				when(userRepository.findByOwnerIdOrThrow(anyLong()))
					.thenThrow(new StoreException(StoreExceptionCode.OWNER_NOT_FOUND));

				// When & Then
				assertThatThrownBy(() -> storeService.createStore(ownerId, requestDto))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.OWNER_NOT_FOUND.getMessage());
			}
		}

		@Nested
		@DisplayName("CEO가 아닌 사용자가 요청하면")
		class Context_with_non_ceo_user {

			@Test
			@DisplayName("NOT_CEO 예외를 던진다")
			void it_throws_not_ceo_exception() {
				// Given
				User nonCeoUser = defaultUser(Role.ROLE_USER);

				when(userRepository.findByOwnerIdOrThrow(1L)).thenReturn(nonCeoUser);

				// When & Then
				assertThatThrownBy(() -> storeService.createStore(ownerId, requestDto))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_CEO.getMessage());
			}
		}

		@Nested
		@DisplayName("사용자가 이미 최대 개수의 가게를 가지고 있으면")
		class Context_with_user_having_max_stores {

			@Test
			@DisplayName("TOO_MANY_STORE 예외를 던진다")
			void it_throws_too_many_store_exception() {
				// Given
				User ceoUser = defaultUser(Role.ROLE_CEO);

				when(userRepository.findByOwnerIdOrThrow(1L)).thenReturn(ceoUser);
				when(storeRepository.countByOwner(ceoUser)).thenReturn(3L); // 최대 개수 이상

				// When & Then
				assertThatThrownBy(() -> storeService.createStore(ownerId, requestDto))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.TOO_MANY_STORE.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("updateStore 메서드는")
	class Describe_updateStore {

		// 테스트에 사용할 공통 값
		Long storeId = 1L;
		Long userId = 1L;
		StoreUpdateRequestDto requestDto = StoreUpdateRequestDto.builder()
			.name("Updated Store")
			.category("Updated Category")
			.city("Updated City")
			.district("Updated District")
			.detailedAddress("Updated Detailed Address")
			.storePictureUrl("http://example.com/updated-image.jpg")
			.introduce("Updated introduce")
			.openTime(LocalTime.of(10, 0))
			.closeTime(LocalTime.of(22, 0))
			.storeStatus(StoreStatus.OPEN)
			.build();

		@Nested
		@DisplayName("유효한 사용자와 가게 ID가 주어지면")
		class Context_with_valid_user_and_store_id {

			@Test
			@DisplayName("가게 정보를 업데이트하고 반환한다")
			void it_updates_and_returns_store() {
				// Given
				User ceoUser = defaultUser(Role.ROLE_CEO);
				Store store = StoreFixture.storeFrom(storeId, ceoUser);

				when(storeRepository.findByIdOrElseThrow(storeId)).thenReturn(store);
				when(userRepository.findByOwnerIdOrThrow(userId)).thenReturn(ceoUser);

				// When
				Store result = storeService.updateStore(storeId, userId, requestDto);

				// Then
				assertThat(result.getName()).isEqualTo(requestDto.getName());
				assertThat(result.getCategory()).isEqualTo(requestDto.getCategory());
				assertThat(result.getAddress().getCity()).isEqualTo(requestDto.getCity());
				assertThat(result.getAddress().getDistrict()).isEqualTo(requestDto.getDistrict());
				assertThat(result.getAddress().getDetailedAddress()).isEqualTo(requestDto.getDetailedAddress());
				assertThat(result.getStorePictureUrl()).isEqualTo(requestDto.getStorePictureUrl());
				assertThat(result.getIntroduce()).isEqualTo(requestDto.getIntroduce());
				assertThat(result.getOperatingHours().getOpenTime()).isEqualTo(requestDto.getOpenTime());
				assertThat(result.getOperatingHours().getCloseTime()).isEqualTo(requestDto.getCloseTime());
				assertThat(result.getStoreStatus()).isEqualTo(requestDto.getStoreStatus());
			}
		}

		@Nested
		@DisplayName("존재하지 않는 가게 ID가 주어지면")
		class Context_with_non_existent_store_id {

			@Test
			@DisplayName("NOT_FOUND_STORE 예외를 던진다")
			void it_throws_not_found_store_exception() {
				// Given
				when(storeRepository.findByIdOrElseThrow(storeId))
					.thenThrow(new StoreException(StoreExceptionCode.NOT_FOUND_STORE));

				// When & Then
				assertThatThrownBy(() -> storeService.updateStore(storeId, userId, requestDto))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_FOUND_STORE.getMessage());
			}
		}

		@Nested
		@DisplayName("CEO가 아닌 사용자가 요청하면")
		class Context_with_non_ceo_user {

			@Test
			@DisplayName("NOT_CEO 예외를 던진다")
			void it_throws_not_ceo_exception() {
				// Given
				User nonCeoUser = defaultUser(Role.ROLE_USER);
				Store store = mock(Store.class);

				when(storeRepository.findByIdOrElseThrow(storeId)).thenReturn(store);
				when(userRepository.findByOwnerIdOrThrow(userId)).thenReturn(nonCeoUser);

				// When & Then
				assertThatThrownBy(() -> storeService.updateStore(storeId, userId, requestDto))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_CEO.getMessage());
			}
		}

		@Nested
		@DisplayName("가게 소유자가 아닌 사용자가 요청하면")
		class Context_with_non_owner_user {

			@Test
			@DisplayName("NOT_STORE_OWNER 예외를 던진다")
			void it_throws_not_store_owner_exception() {
				// Given
				User ceoUser = defaultUser(Role.ROLE_CEO);
				Store store = mock(Store.class);

				when(storeRepository.findByIdOrElseThrow(storeId)).thenReturn(store);
				when(userRepository.findByOwnerIdOrThrow(userId)).thenReturn(ceoUser);
				when(store.isOwner(ceoUser)).thenReturn(false);

				// When & Then
				assertThatThrownBy(() -> storeService.updateStore(storeId, userId, requestDto))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_STORE_OWNER.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("getStoreWithMenus 메서드는")
	class Describe_getStoreWithMenus {

		// 테스트에 사용할 공통 값
		Long storeId = 1L;

		@Nested
		@DisplayName("유효한 가게 ID가 주어지면")
		class Context_with_valid_store_id {

			@Test
			@DisplayName("메뉴 정보를 포함한 가게 정보를 반환한다")
			void it_returns_store_with_menus() {
				// Given
				User ceoUser = defaultUser(Role.ROLE_CEO);
				Store store = StoreFixture.storeFrom(storeId, ceoUser);

				when(storeRepository.findStoreWithMenusByIdOrElseThrow(storeId)).thenReturn(store);

				// When
				Store result = storeService.getStoreWithMenus(storeId);

				// Then
				assertThat(result).isNotNull();
				assertThat(result.getId()).isEqualTo(storeId);
				verify(storeRepository).findStoreWithMenusByIdOrElseThrow(storeId);
			}
		}

		@Nested
		@DisplayName("존재하지 않는 가게 ID가 주어지면")
		class Context_with_non_existent_store_id {

			@Test
			@DisplayName("NOT_FOUND_STORE 예외를 던진다")
			void it_throws_not_found_store_exception() {
				// Given
				when(storeRepository.findStoreWithMenusByIdOrElseThrow(storeId))
					.thenThrow(new StoreException(StoreExceptionCode.NOT_FOUND_STORE));

				// When & Then
				assertThatThrownBy(() -> storeService.getStoreWithMenus(storeId))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_FOUND_STORE.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("deleteStore 메서드는")
	class Describe_deleteStore {

		// 테스트에 사용할 공통 값
		Long storeId = 1L;
		Long userId = 1L;

		@Nested
		@DisplayName("유효한 사용자와 가게 ID가 주어지면")
		class Context_with_valid_user_and_store_id {

			@Test
			@DisplayName("가게를 삭제한다")
			void it_deletes_store() {
				// Given
				User owner = defaultUser(Role.ROLE_CEO);
				Store store = StoreFixture.storeFrom(storeId, owner);

				when(storeRepository.findByIdOrElseThrow(storeId)).thenReturn(store);
				when(userRepository.findByOwnerIdOrThrow(userId)).thenReturn(owner);

				// When
				storeService.deleteStore(storeId, userId);

				// Then
				verify(storeRepository).delete(store);
			}
		}

		@Nested
		@DisplayName("존재하지 않는 가게 ID가 주어지면")
		class Context_with_non_existent_store_id {

			@Test
			@DisplayName("NOT_FOUND_STORE 예외를 던진다")
			void it_throws_not_found_store_exception() {
				// Given
				when(storeRepository.findByIdOrElseThrow(storeId))
					.thenThrow(new StoreException(StoreExceptionCode.NOT_FOUND_STORE));

				// When & Then
				assertThatThrownBy(() -> storeService.deleteStore(storeId, userId))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_FOUND_STORE.getMessage());
			}
		}

		@Nested
		@DisplayName("CEO가 아닌 사용자가 요청하면")
		class Context_with_non_ceo_user {

			@Test
			@DisplayName("NOT_CEO 예외를 던진다")
			void it_throws_not_ceo_exception() {
				// Given
				User nonCeoUser = defaultUser(Role.ROLE_USER);
				Store store = mock(Store.class);

				when(storeRepository.findByIdOrElseThrow(storeId)).thenReturn(store);
				when(userRepository.findByOwnerIdOrThrow(userId)).thenReturn(nonCeoUser);

				// When & Then
				assertThatThrownBy(() -> storeService.deleteStore(storeId, userId))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_CEO.getMessage());
			}
		}

		@Nested
		@DisplayName("가게 소유자가 아닌 사용자가 요청하면")
		class Context_with_non_owner_user {

			@Test
			@DisplayName("NOT_STORE_OWNER 예외를 던진다")
			void it_throws_not_store_owner_exception() {
				// Given
				User ceoUser = defaultUser(Role.ROLE_CEO);
				Store store = mock(Store.class);

				when(storeRepository.findByIdOrElseThrow(storeId)).thenReturn(store);
				when(userRepository.findByOwnerIdOrThrow(userId)).thenReturn(ceoUser);
				when(store.isOwner(ceoUser)).thenReturn(false);

				// When & Then
				assertThatThrownBy(() -> storeService.deleteStore(storeId, userId))
					.isInstanceOf(StoreException.class)
					.hasMessage(StoreExceptionCode.NOT_STORE_OWNER.getMessage());
			}
		}
	}
}
