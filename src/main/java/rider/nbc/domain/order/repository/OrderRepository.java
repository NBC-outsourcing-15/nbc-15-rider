package rider.nbc.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rider.nbc.domain.order.entity.Order;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
}
