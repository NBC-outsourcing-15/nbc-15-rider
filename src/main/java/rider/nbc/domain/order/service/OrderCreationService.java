package rider.nbc.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rider.nbc.domain.cart.entity.Cart;
import rider.nbc.domain.cart.exception.CartException;
import rider.nbc.domain.cart.exception.CartExceptionCode;
import rider.nbc.domain.cart.repository.CartRedisRepository;
import rider.nbc.domain.cart.vo.MenuItem;
import rider.nbc.domain.order.dto.responseDto.OrderResponseDto;
import rider.nbc.domain.order.entity.Order;
import rider.nbc.domain.order.entity.OrderMenu;
import rider.nbc.domain.order.enums.OrderStatus;
import rider.nbc.domain.order.exception.OrderException;
import rider.nbc.domain.order.exception.OrderExceptionCode;
import rider.nbc.domain.order.repository.OrderMenuRepository;
import rider.nbc.domain.order.repository.OrderRepository;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.entity.StoreStatus;
import rider.nbc.domain.store.repository.StoreRepository;
import rider.nbc.domain.user.entity.User;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderCreationService {

    private final CartRedisRepository cartRedisRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;

    @Transactional
    public OrderResponseDto create(User authUser){
        //유저의 장바구니 조회
        Cart cart = cartRedisRepository.findById(authUser.getId())
                .orElseThrow(() -> new CartException(CartExceptionCode.CART_IS_EMPTY));
        //장바구니 비어있으면 xx
        if(cart.getMenus().isEmpty()){
            throw new CartException(CartExceptionCode.CART_IS_EMPTY);
        }

        // Order 만들기
        Store store = storeRepository.findByIdOrElseThrow(cart.getStoreId());
        if(! store.getStoreStatus().equals(StoreStatus.OPEN)){
            throw new OrderException(OrderExceptionCode.STORE_UNAVAILABLE);
        }

        // 최소주문금액
        Long totalPrice = cart.getMenus().stream()
                .mapToLong(MenuItem::getPrice)
                .sum();
        // 쿠폰적용 계산?
        checkPaymentAvailable(totalPrice, store.getMinDeliveryPrice(), authUser.getPoint()); //결제 ㄱㄴ?

        Order order = Order.builder()
                .store(store)
                .user(authUser)
                .totalPrice(totalPrice)
                .orderStatus(OrderStatus.WAITING)
                .build();
        Order savedOrder = orderRepository.save(order);
        // OrderMenu
        List<OrderMenu> orderMenus = cart.getMenus().stream()
                .map(menuItem -> new OrderMenu(order, menuItem))
                .toList();
        orderMenuRepository.saveAll(orderMenus);

        // 장바구니 비우기
        cartRedisRepository.deleteById(authUser.getId());

        return OrderResponseDto.builder()
                .orderId(savedOrder.getId())
                .status(savedOrder.getOrderStatus())
                .userId(authUser.getId())
                .storeId(store.getId())
                .createdAt(savedOrder.getCreatedAt())
                .updatedAt(savedOrder.getUpdatedAt())
                .totalPrice(totalPrice)
                .orderMenus(cart.getMenus())
                .build();
    }

    private void checkPaymentAvailable(Long totalPrice, Long minPrice, Long point) {
        if (totalPrice < minPrice) {
            throw new OrderException(OrderExceptionCode.NOT_MET_ORDER_AMOUNT);
        }

        if( point < totalPrice){
            throw new OrderException(OrderExceptionCode.NOT_ENOUGH_POINT);
        }
    }
}
