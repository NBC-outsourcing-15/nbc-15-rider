package rider.nbc.domain.cart.dto.response;

import java.util.List;

public class CartListResponseDto {
    private Long userId;
    private Long storeId;
    private String storeName;
    private List<CartItemResponseDto> cartMenus;
}
