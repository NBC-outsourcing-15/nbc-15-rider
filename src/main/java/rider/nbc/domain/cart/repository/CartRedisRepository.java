package rider.nbc.domain.cart.repository;

import org.springframework.data.repository.CrudRepository;
import rider.nbc.domain.cart.vo.Cart;

public interface CartRedisRepository extends CrudRepository<Cart, Long> {
}
