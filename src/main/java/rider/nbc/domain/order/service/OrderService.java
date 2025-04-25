package rider.nbc.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rider.nbc.domain.cart.vo.Cart;
import rider.nbc.domain.cart.exception.CartException;
import rider.nbc.domain.cart.exception.CartExceptionCode;
import rider.nbc.domain.cart.repository.CartRedisRepository;
import rider.nbc.domain.cart.vo.MenuItem;
import rider.nbc.domain.notification.service.NotificationService;
import rider.nbc.domain.order.dto.requestDto.OrderStatusRequestDto;
import rider.nbc.domain.order.dto.responseDto.OrderResponseDto;
import rider.nbc.domain.order.entity.Order;
import rider.nbc.domain.order.vo.OrderMenu;
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

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartRedisRepository cartRedisRepository;
    private final StoreRepository storeRepository;
    private final NotificationService notificationService;


    @Transactional
    public OrderResponseDto createOrder(Long authId){
        User authUser = userRepository.findActiveByIdOrThrow(authId);

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
        authUser.minusPoint(totalPrice);

        List<OrderMenu> list = cart.getMenus().stream().map(OrderMenu::from).toList();

        Order order = Order.builder()
                .store(store)
                .user(authUser)
                .totalPrice(totalPrice)
                .orderStatus(OrderStatus.WAITING)
                .orderMenus(list)
                .build();
        Order savedOrder = orderRepository.save(order);

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
                .orderMenus(order.getOrderMenus())
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


    @Transactional
    public String patchOrderStatus(AuthUser authUser, Long orderId, OrderStatusRequestDto statusRequestDto) {
        if( ! authUser.getRole().equals(Role.ROLE_CEO)){
            throw new OrderException(OrderExceptionCode.NOT_OWNER);
        }
        //orderId의 가게를 받아옴
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new OrderException(OrderExceptionCode.INVALID_ORDER_ID));

        //가게의 사장님아이디가 로그인한 사람의 아이디랑 같은지 확인
        Long storeOwner = order.getStore().getOwner().getId();
        if(! storeOwner.equals(authUser.getId())){
            throw new OrderException(OrderExceptionCode.NOT_OWNER); //틀리면 권한부족
        }

        OrderStatus orderStatus = OrderStatus.of(statusRequestDto.getOrderStatus());
        // 맞으면 주문 상태 변경 (같은 상태인건 변경불가능, 취소된 주문도 변경불가능, 완료도 불가능)
        // 대기->준비중->완료 단계별로만 움직일 수 있음.
        checkCanChangeStatus(order.getOrderStatus(), orderStatus);

        order.updateStatus(orderStatus);
        notificationService.sendOrderStatusNotification(
                authUser.getId(), // 주문 작성자 ID
                orderId,
                orderStatus // OrderStatus enum
        );


        return orderStatus.getDisplayName();
    }

    private void checkCanChangeStatus(OrderStatus currentStatus, OrderStatus changeStatus) {
        if (!currentStatus.canTransitionTo(changeStatus)) {
            throw new OrderException(OrderExceptionCode.CANT_CHANGE_STATUS);
        }
    }
}
