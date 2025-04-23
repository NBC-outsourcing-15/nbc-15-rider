package rider.nbc.domain.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import rider.nbc.domain.cart.vo.MenuItem;

import java.util.List;

@Getter
public class CartUpdateRequestDto {

    @Min(value = 1)
    @NotNull( message = "가게 선택은 필수입니다.")
    private Long storeId;


    private List<MenuItem> cartMenus;
}
