package rider.nbc.domain.order.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rider.nbc.domain.cart.vo.MenuItem;
import rider.nbc.domain.order.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class OrderResponseDto {

    private final Long orderId;

    private final Long userId;

    private final Long storeId;

    private final Long totalPrice;

    private final OrderStatus status;

    private final List<MenuItem> orderMenus;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
