package rider.nbc.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rider.nbc.domain.order.entity.Order;
import rider.nbc.domain.order.exception.OrderException;
import rider.nbc.domain.order.exception.OrderExceptionCode;

import java.util.Optional;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    default Order findByIdOrElseThrow(Long userId,Long orderId){
        return findByIdAndUserId(orderId, userId)
                .orElseThrow(()-> new OrderException(OrderExceptionCode.INVALID_ORDER_ID));
    }
}
