package rider.nbc.domain.order.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import rider.nbc.domain.cart.entity.Cart;
import rider.nbc.domain.cart.exception.CartException;
import rider.nbc.domain.cart.repository.CartRedisRepository;
import rider.nbc.domain.cart.vo.MenuItem;
import rider.nbc.domain.order.dto.responseDto.OrderResponseDto;
import rider.nbc.domain.order.entity.Order;
import rider.nbc.domain.order.enums.OrderStatus;
import rider.nbc.domain.order.exception.OrderException;
import rider.nbc.domain.order.repository.OrderMenuRepository;
import rider.nbc.domain.order.repository.OrderRepository;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.entity.StoreStatus;
import rider.nbc.domain.store.repository.StoreRepository;
import rider.nbc.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderCreationServiceTest {
    @InjectMocks
    private OrderCreationService orderCreationService;

    @Mock
    private CartRedisRepository cartRedisRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMenuRepository orderMenuRepository;

    @Test
    @DisplayName("create 성공 -정상_생성 ")
    void createOrder_success() {
        // given
        User user = User.builder().id(1L).point(30000L).build();

        // Cart 생성
        MenuItem menuItem = MenuItem.builder()
                .menuId(1L)
                .price(10000L)
                .name("짜장면")
                .quantity(1)
                .build();

        Cart cart = new Cart(); // 기본 생성자
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

        given(cartRedisRepository.findById(1L)).willReturn(Optional.of(cart));
        given(storeRepository.findByIdOrElseThrow(10L)).willReturn(store);
        given(orderRepository.save(any(Order.class))).willReturn(savedOrder);

        // when
        OrderResponseDto responseDto = orderCreationService.create(user);

        // then
        assertEquals(100L, responseDto.getOrderId());
        assertEquals(10000L, responseDto.getTotalPrice());
        assertEquals(OrderStatus.WAITING, responseDto.getStatus());
        assertEquals(1L, responseDto.getUserId());
        assertEquals(10L, responseDto.getStoreId());
        assertEquals(1, responseDto.getOrderMenus().size());
        verify(cartRedisRepository).deleteById(1L);
        verify(orderMenuRepository).saveAll(anyList());
    }


    @Test
    @DisplayName("create 실패 - 장바구니 비었음 ")
    void createOrder_fail_whenCartEmpty() {
        // given
        Long userId = 1L;
        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        given(cartRedisRepository.findById(userId)).willReturn(Optional.empty());

        // expect
        assertThrows(CartException.class, () -> orderCreationService.create(user));
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
        given(cartRedisRepository.findById(userId)).willReturn(Optional.of(cart));
        given(storeRepository.findByIdOrElseThrow(storeId)).willReturn(closedStore);

        // expect
        assertThrows(OrderException.class, () -> orderCreationService.create(user));
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

        given(cartRedisRepository.findById(userId)).willReturn(Optional.of(cart));
        given(storeRepository.findByIdOrElseThrow(storeId)).willReturn(store);

        // expect
        assertThrows(OrderException.class, () -> orderCreationService.create(user));
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
        given(cartRedisRepository.findById(userId)).willReturn(Optional.of(cart));
        given(storeRepository.findByIdOrElseThrow(storeId)).willReturn(store);

        // expect
        assertThrows(OrderException.class, () -> orderCreationService.create(user));
    }
}