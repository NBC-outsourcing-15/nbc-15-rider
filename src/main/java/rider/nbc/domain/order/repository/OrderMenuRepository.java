package rider.nbc.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rider.nbc.domain.order.entity.OrderMenu;

@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenu,Long> {
}
