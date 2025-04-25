package rider.nbc.domain.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.order.entity.Order;
import rider.nbc.domain.order.enums.OrderStatus;
import rider.nbc.domain.order.exception.OrderException;
import rider.nbc.domain.order.exception.OrderExceptionCode;
import rider.nbc.domain.order.repository.OrderRepository;
import rider.nbc.domain.order.vo.OrderMenu;
import rider.nbc.domain.review.dto.request.StoreReviewCreateRequest;
import rider.nbc.domain.review.dto.request.StoreReviewUpdateRequest;
import rider.nbc.domain.review.dto.response.StoreReviewResponse;
import rider.nbc.domain.review.entity.StoreReview;
import rider.nbc.domain.review.exception.StoreReviewException;
import rider.nbc.domain.review.exception.code.StoreReviewExceptionCode;
import rider.nbc.domain.review.repository.StoreReviewRepository;
import rider.nbc.domain.review.vo.MenuReview;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.exception.StoreException;
import rider.nbc.domain.store.exception.StoreExceptionCode;
import rider.nbc.domain.store.repository.StoreRepository;
import rider.nbc.domain.user.entity.Role;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.exception.UserException;
import rider.nbc.domain.user.exception.UserExceptionCode;
import rider.nbc.domain.user.repository.UserRepository;
import rider.nbc.global.auth.AuthUser;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StoreReviewServiceTest {
    @Mock
    private StoreReviewRepository storeReviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreReviewService storeReviewService;

    @Spy
    private Order order;

    @Spy
    private User user;

    @Spy
    private Store store;

    @Spy
    private StoreReview storeReview;

    private final AuthUser authUser = AuthUser.builder()
            .id(1L)
            .nickname("test")
            .email("test@test.com")
            .role(Role.ROLE_USER)
            .build();

    private final OrderMenu orderMenu = OrderMenu.builder()
            .menuName("test menu")
            .price(1000L)
            .quantity(1)
            .build();

    private final Menu menu = Menu.builder()
            .id(1L)
            .name("test menu")
            .build();

    private final MenuReview menuReview = MenuReview.builder()
            .menuName("test menu")
            .content("강추")
            .build();

    private final StoreReviewUpdateRequest storeReviewUpdateRequest =
            new StoreReviewUpdateRequest("맛있고 또 맛있다", 5);

    private StoreReviewCreateRequest storeReviewCreateRequest = new StoreReviewCreateRequest("맛있다", 4
            , List.of(new StoreReviewCreateRequest.MenuReviewRequest("test menu", "강추")));

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();

        store = Store.builder()
                .id(1L)
                .menus(List.of(menu))
                .build();

        order = Order.builder()
                .id(1L)
                .orderStatus(OrderStatus.DONE)
                .user(user)
                .store(store)
                .orderMenus(List.of(orderMenu))
                .build();

        storeReview = StoreReview.builder()
                .id(1L)
                .user(user)
                .store(store)
                .order(order)
                .menuReviews(Set.of(menuReview))
                .build();

    }

    @Nested
    @DisplayName("리뷰 생성 테스트")
    class CreateReviewTest {
        @Test
        @DisplayName("리뷰 생성 성공")
        void success_createStoreReview() {
            // Given
            given(storeReviewRepository.existsByOrder_Id(anyLong()))
                    .willReturn(false);
            given(orderRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(order));
            given(storeRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(store));
            given(storeReviewRepository.save(any()))
                    .willReturn(storeReview);

            // When
            StoreReviewResponse response = storeReviewService.createStoreReview(storeReviewCreateRequest, authUser
                    , 1L, 1L);

            // Then
            assertThat(response)
                    .isNotNull();
            assertThat(response.getReviewId())
                    .isEqualTo(storeReview.getId());
            assertThat(response.getContent())
                    .isEqualTo(storeReview.getContent());
            assertThat(response.getRating())
                    .isEqualTo(storeReview.getRating());
            assertThat(response.getStoreId())
                    .isEqualTo(store.getId());
            assertThat(response.getOrderId())
                    .isEqualTo(order.getId());
            assertThat(response.getMenuReviews())
                    .isNotEmpty()
                    .isEqualTo(storeReview.getMenuReviews());

        }

        @Test
        @DisplayName("리뷰 생성 실패 - 이미 존재하는 리뷰")
        void fail_createStoreReview_alreadyExistsReview() {
            // Given
            given(storeReviewRepository.existsByOrder_Id(anyLong()))
                    .willReturn(true);

            // When
            StoreReviewException exception = assertThrows(StoreReviewException.class,
                    () -> storeReviewService.createStoreReview(storeReviewCreateRequest, authUser, 1L, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(StoreReviewExceptionCode.ALREADY_EXISTS_REVIEW);

        }

        @Test
        @DisplayName("리뷰 생성 실패 - 주문을 찾을 수 없음")
        void fail_createStoreReview_invalidOrderId() {
            // Given
            given(storeReviewRepository.existsByOrder_Id(anyLong()))
                    .willReturn(false);
            given(orderRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            OrderException exception = assertThrows(OrderException.class,
                    () -> storeReviewService.createStoreReview(storeReviewCreateRequest, authUser, 1L, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(OrderExceptionCode.INVALID_ORDER_ID);

        }

        @Test
        @DisplayName("리뷰 생성 실패 - 완료된 주문이 아님")
        void fail_createStoreReview_orderNotDone() {
            // Given
            given(storeReviewRepository.existsByOrder_Id(anyLong()))
                    .willReturn(false);
            given(orderRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(order.toBuilder()
                            .orderStatus(OrderStatus.WAITING)
                            .build()));

            // When
            StoreReviewException exception = assertThrows(StoreReviewException.class,
                    () -> storeReviewService.createStoreReview(storeReviewCreateRequest, authUser, 1L, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(StoreReviewExceptionCode.ORDER_NOT_DONE);

        }

        @Test
        @DisplayName("리뷰 생성 실패 - 완료된 주문이 아님")
        void fail_createStoreReview_notOrderer() {
            // Given
            given(storeReviewRepository.existsByOrder_Id(anyLong()))
                    .willReturn(false);
            given(orderRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(order.toBuilder()
                            .user(User.builder()
                                    .id(2L)
                                    .build())
                            .build()));

            // When
            StoreReviewException exception = assertThrows(StoreReviewException.class,
                    () -> storeReviewService.createStoreReview(storeReviewCreateRequest, authUser, 1L, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(StoreReviewExceptionCode.NOT_ORDERER);

        }

        @Test
        @DisplayName("리뷰 생성 실패 - 가게를 찾을 수 없음")
        void fail_createStoreReview_notFoundStore() {
            // Given
            given(storeReviewRepository.existsByOrder_Id(anyLong()))
                    .willReturn(false);
            given(orderRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(order));
            given(storeRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            StoreException exception = assertThrows(StoreException.class,
                    () -> storeReviewService.createStoreReview(storeReviewCreateRequest, authUser, 1L, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(StoreExceptionCode.NOT_FOUND_STORE);

        }

        @Test
        @DisplayName("리뷰 생성 실패 - 주문한 메뉴가 아님")
        void fail_createStoreReview_notOrderMenu() {
            // Given
            storeReviewCreateRequest = new StoreReviewCreateRequest("test", 1
                    , List.of(new StoreReviewCreateRequest.MenuReviewRequest("임시 메뉴", "테스트")));

            given(storeReviewRepository.existsByOrder_Id(anyLong()))
                    .willReturn(false);
            given(orderRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(order));
            given(storeRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(store));

            // When
            StoreReviewException exception = assertThrows(StoreReviewException.class,
                    () -> storeReviewService.createStoreReview(storeReviewCreateRequest, authUser, 1L, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(StoreReviewExceptionCode.NOT_ORDER_MENU);

        }

    }

    @Nested
    @DisplayName("리뷰 수정 테스트")
    class UpdateStoreReviewTest {
        @Test
        @DisplayName("리뷰 수정 성공")
        void success_updateStoreReview() {
            // Given
            given(storeReviewRepository.findById(anyLong()))
                    .willReturn(Optional.of(storeReview.toBuilder()
                            .content(storeReviewUpdateRequest.getContent())
                            .rating(storeReviewUpdateRequest.getRating())
                            .build()));
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(user));

            // When
            StoreReviewResponse response = storeReviewService.updateStoreReview(storeReviewUpdateRequest, authUser
                    , 1L);

            // Then
            assertThat(response)
                    .isNotNull();
            assertThat(response.getReviewId())
                    .isEqualTo(storeReview.getId());
            assertThat(response.getContent())
                    .isEqualTo(storeReviewUpdateRequest.getContent());
            assertThat(response.getRating())
                    .isEqualTo(storeReviewUpdateRequest.getRating());
            assertThat(response.getStoreId())
                    .isEqualTo(store.getId());
            assertThat(response.getOrderId())
                    .isEqualTo(order.getId());
            assertThat(response.getMenuReviews())
                    .isNotEmpty()
                    .isEqualTo(storeReview.getMenuReviews());

        }

        @Test
        @DisplayName("리뷰 수정 실패 - 리뷰를 찾을 수 없음")
        void fail_updateStoreReview_reviewNotFound() {
            // Given
            given(storeReviewRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            StoreReviewException exception = assertThrows(StoreReviewException.class,
                    () -> storeReviewService.updateStoreReview(storeReviewUpdateRequest, authUser, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(StoreReviewExceptionCode.REVIEW_NOT_FOUND);

        }

        @Test
        @DisplayName("리뷰 수정 실패 - 이미 삭제된 리뷰")
        void fail_updateStoreReview_alreadyDeleted() {
            // Given
            given(storeReviewRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(storeReview.toBuilder()
                            .isDeleted(true)
                            .build()));

            // When
            StoreReviewException exception = assertThrows(StoreReviewException.class,
                    () -> storeReviewService.updateStoreReview(storeReviewUpdateRequest, authUser, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(StoreReviewExceptionCode.ALREADY_DELETED);

        }

        @Test
        @DisplayName("리뷰 수정 실패 - 유저를 찾을 수 없음")
        void fail_updateStoreReview_userNotFound() {
            // Given
            given(storeReviewRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(storeReview));
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            UserException exception = assertThrows(UserException.class,
                    () -> storeReviewService.updateStoreReview(storeReviewUpdateRequest, authUser, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(UserExceptionCode.USER_NOT_FOUND);

        }

        @Test
        @DisplayName("리뷰 수정 실패 - 리뷰를 작성한 유저가 아님")
        void fail_updateStoreReview_reviewAuthorMismatch() {
            // Given
            given(storeReviewRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(storeReview.toBuilder()
                            .user(User.builder()
                                    .id(2L)
                                    .build())
                            .build()));
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(user));

            // When
            StoreReviewException exception = assertThrows(StoreReviewException.class,
                    () -> storeReviewService.updateStoreReview(storeReviewUpdateRequest, authUser, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(StoreReviewExceptionCode.REVIEW_AUTHOR_MISMATCH);

        }

    }

    @Nested
    @DisplayName("리뷰 삭제 테스트")
    class DeleteStoreReviewTest {
        @Test
        @DisplayName("리뷰 삭제 성공")
        void success_deleteStoreReview() {
            // Given
            given(storeReviewRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(storeReview));
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(user));

            // When
            Long deleteStoreReviewId = storeReviewService.deleteStoreReview(authUser, 1L, 1L);

            // Then
            assertThat(deleteStoreReviewId)
                    .isEqualTo(storeReview.getId());
            assertThat(storeReview.isDeleted())
                    .isTrue();

        }

        @Test
        @DisplayName("리뷰 삭제 실패 - 리뷰를 찾을 수 없음")
        void fail_deleteStoreReview_reviewNotFound() {
            // Given
            given(storeReviewRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            StoreReviewException exception = assertThrows(StoreReviewException.class,
                    () -> storeReviewService.deleteStoreReview(authUser, 1L, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(StoreReviewExceptionCode.REVIEW_NOT_FOUND);

        }

        @Test
        @DisplayName("리뷰 삭제 실패 - 이미 삭제된 리뷰")
        void fail_deleteStoreReview_alreadyDeleted() {
            // Given
            given(storeReviewRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(storeReview.toBuilder()
                            .isDeleted(true)
                            .build()));

            // When
            StoreReviewException exception = assertThrows(StoreReviewException.class,
                    () -> storeReviewService.deleteStoreReview(authUser, 1L, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(StoreReviewExceptionCode.ALREADY_DELETED);

        }

        @Test
        @DisplayName("리뷰 삭제 실패 - 유저를 찾을 수 없음")
        void fail_deleteStoreReview_userNotFound() {
            // Given
            given(storeReviewRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(storeReview));
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            UserException exception = assertThrows(UserException.class,
                    () -> storeReviewService.deleteStoreReview(authUser, 1L, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(UserExceptionCode.USER_NOT_FOUND);

        }

        @Test
        @DisplayName("리뷰 삭제 실패 - 리뷰를 작성한 유저가 아님")
        void fail_deleteStoreReview_reviewAuthorMismatch() {
            // Given
            given(storeReviewRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(storeReview.toBuilder()
                            .user(User.builder()
                                    .id(2L)
                                    .build())
                            .build()));
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(user));

            // When
            StoreReviewException exception = assertThrows(StoreReviewException.class,
                    () -> storeReviewService.deleteStoreReview(authUser, 1L, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(StoreReviewExceptionCode.REVIEW_AUTHOR_MISMATCH);

        }

        @Test
        @DisplayName("리뷰 삭제 실패 - 해당 가게의 리뷰가 아님")
        void fail_deleteStoreReview_reviewNotBelongToStore() {
            // Given
            given(storeReviewRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(storeReview));
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(user));

            // When
            StoreReviewException exception = assertThrows(StoreReviewException.class,
                    () -> storeReviewService.deleteStoreReview(authUser, 2L, 1L));

            // Then
            assertThat(exception.getErrorCode())
                    .isEqualTo(StoreReviewExceptionCode.REVIEW_NOT_BELONG_TO_STORE);

        }

    }

}
