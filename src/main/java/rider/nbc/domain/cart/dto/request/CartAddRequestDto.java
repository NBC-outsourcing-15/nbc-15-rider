package rider.nbc.domain.cart.dto.request;

import lombok.Getter;

@Getter
public class CartAddRequestDto {
    private Long menuId;
    private int quantity;
    //private List<int> options;
}
