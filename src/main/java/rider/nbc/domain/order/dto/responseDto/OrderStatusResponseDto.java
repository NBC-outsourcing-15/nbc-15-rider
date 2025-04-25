package rider.nbc.domain.order.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import rider.nbc.domain.order.enums.OrderStatus;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class OrderStatusResponseDto {
    private final Long orderId;
    private final Long storeId;
    private final OrderStatus orderStatus;
    private final LocalDateTime updateAt;
}
