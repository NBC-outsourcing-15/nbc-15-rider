package rider.nbc.domain.cart.repository;

import org.springframework.data.repository.CrudRepository;
import rider.nbc.domain.cart.entity.Cart;
import rider.nbc.domain.cart.vo.MenuItem;

public interface CartRedisRepository extends CrudRepository<Cart, Long> {

    //void addCartItem(Long userId, Long storeId, MenuItem value);
}
