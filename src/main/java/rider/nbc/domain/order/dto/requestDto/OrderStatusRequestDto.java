package rider.nbc.domain.order.dto.requestDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rider.nbc.domain.order.enums.OrderStatus;

@Getter
@RequiredArgsConstructor
public class OrderStatusRequestDto {
    private final OrderStatus orderStatus;
}
