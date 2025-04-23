package rider.nbc.domain.cart.dto.response;


import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class CartItemResponseDto {
    private final Long storeId;
    private final Long menuId;

    private final int price;
    private final int quantity;
    //private List<int> options;
}
