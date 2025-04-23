package rider.nbc.domain.cart.repository;

import org.springframework.data.repository.CrudRepository;
import rider.nbc.domain.cart.entity.Cart;

public interface CartRedisRepository extends CrudRepository<Cart, Long> {
    void removeCartByUserid(Long userid);

    //void addCartItem(Long userId, Long storeId, MenuItem value);
}
