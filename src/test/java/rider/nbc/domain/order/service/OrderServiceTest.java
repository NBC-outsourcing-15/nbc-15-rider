package rider.nbc.domain.order.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import rider.nbc.domain.order.dto.requestDto.OrderStatusRequestDto;
import rider.nbc.domain.order.enums.OrderStatus;
import rider.nbc.domain.order.exception.OrderException;
import rider.nbc.domain.order.repository.OrderRepository;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.user.entity.Role;
import rider.nbc.domain.user.entity.User;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.domain.order.entity.Order;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Test
    @DisplayName("patchOrderStatus 성공")
    void patchOrderStatus_success() {
        // given
        AuthUser authUser = new AuthUser(1L,"exam@exam.com", "사장",Role.CEO);

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
        String result = orderService.patchOrderStatus(authUser, 1L, dto);

        // then
        assertEquals("조리중", result);
        assertEquals(OrderStatus.ACCEPTED, order.getOrderStatus());
    }

    @Test
    @DisplayName("patchOrderStatus 실패 - 사장 role 아님")
    void patchOrderStatus_fail_notCEO() {
        // given
        AuthUser authUser = new AuthUser(1L,"exam@exam.com", "손님",Role.USER);
        OrderStatusRequestDto dto = new OrderStatusRequestDto("ACCEPTED");

        // when & then
        assertThrows(OrderException.class, () ->
                orderService.patchOrderStatus(authUser, 1L, dto));
    }

    @Test
    @DisplayName("patchOrderStatus 실패 - 본인 가게 아님")
    void patchOrderStatus_fail_NotMyStore() {
        // given
        AuthUser authUser = new AuthUser(1L,"exam@exam.com", "사장",Role.CEO);
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
        AuthUser authUser = new AuthUser(1L,"exam@exam.com", "사장",Role.CEO);

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

}