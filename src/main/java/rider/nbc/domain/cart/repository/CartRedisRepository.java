package rider.nbc.domain.cart.repository;

import rider.nbc.domain.cart.vo.MenuItem;

public interface CartRedisRepository {

    void addCartItem(Long userId, Long storeId, MenuItem value);
}
