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
}