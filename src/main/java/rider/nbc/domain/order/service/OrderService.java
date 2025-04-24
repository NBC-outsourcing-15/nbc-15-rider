package rider.nbc.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rider.nbc.domain.cart.repository.CartRedisRepository;
import rider.nbc.domain.order.dto.responseDto.OrderResponseDto;
import rider.nbc.domain.order.repository.OrderRepository;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.repository.UserRepository;

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
        //유저의 장바구니 조회
        User user = userRepository.findActiveByIdOrThrow(authId);
        return orderCreationService.create(user);
    }
}
