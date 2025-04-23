package rider.nbc.domain.cart.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartAddRequestDto {
    private Long menuId;
    private int quantity;
    //private List<int> options;
}
