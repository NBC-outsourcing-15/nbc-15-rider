package rider.nbc.domain.order.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderStatusRequestDto {

    @NotBlank(message = "상태 값을 입력해주세요")
    private final String orderStatus;
}
