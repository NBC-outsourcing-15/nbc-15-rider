package rider.nbc.domain.cart.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rider.nbc.domain.cart.vo.Cart;
import rider.nbc.domain.cart.vo.MenuItem;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CartListResponseDto {
    private final Long storeId;
    private final List<MenuItem> cartMenus;

    public CartListResponseDto(Cart cart){
        this.storeId = cart.getStoreId();
        this.cartMenus = cart.getMenus();
    }
}
