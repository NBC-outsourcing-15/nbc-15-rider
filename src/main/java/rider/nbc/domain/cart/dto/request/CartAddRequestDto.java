package rider.nbc.domain.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartAddRequestDto {

    @Min(value = 1)
    @NotNull( message = "메뉴 선택은 필수입니다.")
    private Long menuId;

    @Min(value = 1, message = "메뉴는 1개이상부터 선택가능합니다.")
    @NotNull(message = "장바구니에 담으려면 수량을 필수로 입력해야합니다.")
    private int quantity;
}
