package rider.nbc.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rider.nbc.domain.order.entity.Order;
import rider.nbc.domain.order.exception.OrderException;
import rider.nbc.domain.order.exception.OrderExceptionCode;

import java.util.List;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    default Order findByIdOrElseThrow(Long orderId){
        return findById(orderId)
                .orElseThrow(()-> new OrderException(OrderExceptionCode.INVALID_ORDER_ID));
    }

    List<Order> findAllByUserId(Long userId);

    @Query("SELECT o FROM Order o WHERE o.store.owner.id = :ownerId")
    List<Order> findAllByStoreOwnerId(@Param("ownerId") Long ownerId);

}
