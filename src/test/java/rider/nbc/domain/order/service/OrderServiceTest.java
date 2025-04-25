package rider.nbc.domain.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import rider.nbc.domain.cart.vo.Cart;
import rider.nbc.domain.cart.exception.CartException;
import rider.nbc.domain.cart.repository.CartRedisRepository;
import rider.nbc.domain.cart.vo.MenuItem;
import rider.nbc.domain.order.dto.requestDto.OrderStatusRequestDto;
import rider.nbc.domain.order.dto.responseDto.OrderResponseDto;
import rider.nbc.domain.order.dto.responseDto.OrderStatusResponseDto;
import rider.nbc.domain.order.entity.Order;
import rider.nbc.domain.order.enums.OrderStatus;
import rider.nbc.domain.order.exception.OrderException;
import rider.nbc.domain.order.exception.OrderExceptionCode;
import rider.nbc.domain.order.repository.OrderRepository;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.entity.StoreStatus;
import rider.nbc.domain.store.repository.StoreRepository;
import rider.nbc.domain.user.entity.Role;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.repository.UserRepository;
import rider.nbc.global.auth.AuthUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CartRedisRepository cartRedisRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("create 성공 -정상_생성 ")
    void createOrder_success() {
        // given
        User user = User.builder().id(1L).point(30000L).build();

        // Cart 생성
        Cart cart = new Cart();
        MenuItem menuItem = MenuItem.builder()
                .menuId(1L)
                .price(10000L)
                .name("짜장면")
                .quantity(1)
                .build();

        ReflectionTestUtils.setField(cart, "userId", 1L);
        ReflectionTestUtils.setField(cart, "storeId", 10L);
        ReflectionTestUtils.setField(cart, "menus", List.of(menuItem));

        Store store = Store.builder()
                .id(10L)
                .storeStatus(StoreStatus.OPEN)
                .minDeliveryPrice(5000L)
                .build();

        Order savedOrder = Order.builder()
                .id(100L)
                .store(store)
                .user(user)
                .orderStatus(OrderStatus.WAITING)
                .totalPrice(10000L)
                .build();

        given(userRepository.findActiveByIdOrThrow(any())).willReturn(user);
        given(cartRedisRepository.findById(1L)).willReturn(Optional.of(cart));
        given(storeRepository.findByIdOrElseThrow(10L)).willReturn(store);
        given(orderRepository.save(any(Order.class))).willReturn(savedOrder);

        // when
        OrderResponseDto responseDto = orderService.createOrder(1L);

        // then
        assertEquals(100L, responseDto.getOrderId());
        assertEquals(10000L, responseDto.getTotalPrice());
        assertEquals(OrderStatus.WAITING, responseDto.getStatus());
        assertEquals(1L, responseDto.getUserId());
        assertEquals(10L, responseDto.getStoreId());
        assertEquals(1, responseDto.getOrderMenus().size());
        verify(cartRedisRepository).deleteById(1L);
    }


    @Test
    @DisplayName("create 실패 - 장바구니 비었음 ")
    void createOrder_fail_whenCartEmpty() {
        // given
        Long userId = 1L;
        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findActiveByIdOrThrow(any())).willReturn(user);
        given(cartRedisRepository.findById(userId)).willReturn(Optional.empty());

        // expect
        assertThrows(CartException.class, () -> orderService.createOrder(userId));
    }

    @Test
    @DisplayName("create 실패 - 가게 닫았음 ")
    void createOrder_fail_whenStoreClosed() {
        // given
        Long userId = 1L;
        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        Long storeId = 1L;
        Store closedStore = Store.builder()
                .id(storeId)
                .storeStatus(StoreStatus.CLOSED)
                .build();

        MenuItem menuItem = MenuItem.builder()
                .menuId(1L)
                .price(10000L)
                .name("짜장면")
                .quantity(1)
                .build();

        Cart cart = new Cart(userId, storeId, menuItem);
        given(userRepository.findActiveByIdOrThrow(any())).willReturn(user);
        given(cartRedisRepository.findById(userId)).willReturn(Optional.of(cart));
        given(storeRepository.findByIdOrElseThrow(storeId)).willReturn(closedStore);

        // expect
        assertThrows(OrderException.class, () -> orderService.createOrder(userId));
    }

    @Test
    @DisplayName("create 실패 - 최소주문금액 미충족 ")
    void createOrder_fail_whenUnderMinPrice() {
        // given
        Long userId = 1L;
        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "point", 5000L);

        Long storeId = 1L;
        Store store = Store.builder()
                .id(storeId)
                .storeStatus(StoreStatus.OPEN)
                .minDeliveryPrice(7000L)
                .build();

        MenuItem menuItem = MenuItem.builder()
                .menuId(1L)
                .price(10000L)
                .name("짜장면")
                .quantity(1)
                .build();

        Cart cart = new Cart(userId, storeId, menuItem);
        given(userRepository.findActiveByIdOrThrow(any())).willReturn(user);
        given(cartRedisRepository.findById(userId)).willReturn(Optional.of(cart));
        given(storeRepository.findByIdOrElseThrow(storeId)).willReturn(store);

        // expect
        assertThrows(OrderException.class, () -> orderService.createOrder(userId));
    }

    @Test
    @DisplayName("create 실패 - 가진 포인트가 적음 ")
    void createOrder_fail_whenNotEnoughPoints() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).point(1000L).build();

        Long storeId = 1L;
        Store store = Store.builder()
                .id(storeId)
                .storeStatus(StoreStatus.OPEN)
                .minDeliveryPrice(3000L)
                .build();

        MenuItem menuItem = MenuItem.builder()
                .menuId(1L)
                .price(10000L)
                .name("짜장면")
                .quantity(1)
                .build();

        Cart cart = new Cart(userId, storeId, menuItem);
        given(userRepository.findActiveByIdOrThrow(any())).willReturn(user);
        given(cartRedisRepository.findById(userId)).willReturn(Optional.of(cart));
        given(storeRepository.findByIdOrElseThrow(storeId)).willReturn(store);

        // expect
        assertThrows(OrderException.class, () -> orderService.createOrder(userId));
    }

    @Test
    @DisplayName("patchOrderStatus 성공")
    void patchOrderStatus_success() {
        // given
        AuthUser authUser = new AuthUser(1L, "exam@exam.com", "사장", Role.CEO);

        User owner = new User();
        ReflectionTestUtils.setField(owner, "id", 1L);

        Store store = new Store();
        ReflectionTestUtils.setField(store, "owner", owner);

        Order order = new Order();
        ReflectionTestUtils.setField(order, "store", store);
        ReflectionTestUtils.setField(order, "user", owner);
        order.updateStatus(OrderStatus.WAITING);


        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        OrderStatusRequestDto dto = new OrderStatusRequestDto("ACCEPTED");

        // when
        OrderStatusResponseDto result = orderService.patchOrderStatus(authUser, 1L, dto);

        // then
        assertEquals(OrderStatus.ACCEPTED, result.getOrderStatus());
        assertEquals(OrderStatus.ACCEPTED, order.getOrderStatus());
    }

    @Test
    @DisplayName("patchOrderStatus 실패 - 사장 role 아님")
    void patchOrderStatus_fail_notCEO() {
        // given
        AuthUser authUser = new AuthUser(1L, "exam@exam.com", "손님", Role.USER);
        OrderStatusRequestDto dto = new OrderStatusRequestDto("ACCEPTED");

        // when & then
        assertThrows(OrderException.class, () ->
                orderService.patchOrderStatus(authUser, 1L, dto));
    }

    @Test
    @DisplayName("patchOrderStatus 실패 - 본인 가게 아님")
    void patchOrderStatus_fail_NotMyStore() {
        // given
        AuthUser authUser = new AuthUser(1L, "exam@exam.com", "사장", Role.CEO);
        OrderStatusRequestDto dto = new OrderStatusRequestDto("ACCEPTED");

        User realowner = new User();
        ReflectionTestUtils.setField(realowner, "id", 2L);

        Store store = new Store();
        ReflectionTestUtils.setField(store, "owner", realowner);

        Order order = new Order();
        ReflectionTestUtils.setField(order, "store", store);
        ReflectionTestUtils.setField(order, "user", realowner);
        order.updateStatus(OrderStatus.WAITING);

        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        // when & then
        assertThrows(OrderException.class, () ->
                orderService.patchOrderStatus(authUser, 1L, dto));
    }

    @Test
    @DisplayName("patchOrderStatus 실패 - 잘못된_상태변경")
    void patchOrderStatus_fail_InvaliableStatus() {
        // given
        AuthUser authUser = new AuthUser(1L, "exam@exam.com", "사장", Role.CEO);

        User owner = new User();
        ReflectionTestUtils.setField(owner, "id", 1L);

        Store store = new Store();
        ReflectionTestUtils.setField(store, "owner", owner);

        Order order = new Order();
        ReflectionTestUtils.setField(order, "store", store);
        ReflectionTestUtils.setField(order, "user", owner);
        order.updateStatus(OrderStatus.WAITING);

        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        OrderStatusRequestDto dto = new OrderStatusRequestDto("DONE");

        // when & then
        assertThrows(OrderException.class, () ->
                orderService.patchOrderStatus(authUser, 1L, dto));
    }

    @Nested
    @DisplayName("주문 조회")
    class getOrder_Orders_Test {
        Order order;
        AuthUser userAuthUser;
        AuthUser ceoAuthUser;

        @BeforeEach
        void setUp() {
            User user = new User();
            ReflectionTestUtils.setField(user, "id", 1L);

            User ceo = new User();
            ReflectionTestUtils.setField(ceo, "id", 2L);

            Store store = new Store();
            ReflectionTestUtils.setField(store, "owner", ceo);
            ReflectionTestUtils.setField(store, "id", 10L);

            order = new Order();
            ReflectionTestUtils.setField(order, "id", 100L);
            ReflectionTestUtils.setField(order, "store", store);
            ReflectionTestUtils.setField(order, "user", user);
            order.updateStatus(OrderStatus.WAITING);


            userAuthUser = new AuthUser(1L, "e@email.com", "고객", Role.USER);
            ceoAuthUser = new AuthUser(2L, "ce@email.com", "사장", Role.CEO);
        }

        @Test
        @DisplayName("getOrder 성공 - 사용자 본인주문 조회 성공")
        void getOrder_success() {
            // given
            given(orderRepository.findByIdOrElseThrow(100L)).willReturn(order);

            // when
            OrderResponseDto result = orderService.getOrder(userAuthUser, 100L);

            // then
            assertEquals(100L, result.getOrderId());
        }

        @Test
        void getOrder실패_권한없는유저() {
            // given
            AuthUser stranger = new AuthUser(999L,"err@email.com", "고객2", Role.USER);
            given(orderRepository.findByIdOrElseThrow(100L)).willReturn(order);

            // when & then
            OrderException ex = assertThrows(OrderException.class, () ->
                    orderService.getOrder(stranger, 100L));
            assertEquals(OrderExceptionCode.INVALID_ORDER_ID, ex.getErrorCode());

        }

        @Test
        void getAllOrders성공_유저_내주문이력_조회() {
            // given
            given(orderRepository.findAllByUserId(1L)).willReturn(List.of(order));

            // when
            List<OrderResponseDto> results = orderService.getAllOrders(userAuthUser);

            // then
            assertEquals(1, results.size());
            assertEquals(100L, results.get(0).getOrderId());
        }

        @Test
        void getAllOrders성공_사장_내가게주문조회() {
            // given
            given(orderRepository.findAllByStoreOwnerId(2L)).willReturn(List.of(order));

            // when
            List<OrderResponseDto> results = orderService.getAllOrders(ceoAuthUser);

            // then
            assertEquals(1, results.size());
            assertEquals(100L, results.get(0).getOrderId());
        }

    }
}