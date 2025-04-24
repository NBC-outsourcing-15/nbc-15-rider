package rider.nbc.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rider.nbc.domain.order.dto.responseDto.OrderResponseDto;
import rider.nbc.domain.order.entity.Order;
import rider.nbc.domain.order.exception.OrderException;
import rider.nbc.domain.order.exception.OrderExceptionCode;
import rider.nbc.domain.order.repository.OrderRepository;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.repository.UserRepository;
import rider.nbc.global.auth.AuthUser;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private final OrderCreationService orderCreationService;

    public OrderResponseDto createOrder(Long authId){
        User user = userRepository.findActiveByIdOrThrow(authId);
        return orderCreationService.create(user);
    }

    public void patchOrderStatus(AuthUser authUser, Long orderId) {
        //orderId의 가게를 받아옴
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new OrderException(OrderExceptionCode.INVALID_ORDER_ID));

        //가게의 사장님아이디가 로그인한 사람의 아이디랑 같은지 확인

        //틀리면 권한부족

        // 맞으면 주문 상태 변경
        // (같은 상태인건 변경불가능, 취소된 주문도 변경불가능)
    }
}
