package rider.nbc.domain.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import rider.nbc.domain.cart.vo.MenuItem;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CartUpdateRequestDto {

    @Min(value = 1)
    @NotNull( message = "가게 선택은 필수입니다.")
    private final Long storeId;
    private final List<MenuItem> cartMenus;
}
