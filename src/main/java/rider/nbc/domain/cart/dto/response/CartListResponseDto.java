package rider.nbc.domain.cart.dto.response;

import lombok.AllArgsConstructor;
import rider.nbc.domain.cart.entity.Cart;
import rider.nbc.domain.cart.vo.MenuItem;

import java.util.List;

public class CartListResponseDto {
    private Long storeId;
    private List<MenuItem> cartMenus;

    public CartListResponseDto(Cart cart){
        this.storeId = cart.getStoreId();
        this.cartMenus = cart.getMenus();
    }
}
