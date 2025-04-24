package rider.nbc.domain.order.enums;

import lombok.Getter;
import rider.nbc.domain.order.exception.OrderException;
import rider.nbc.domain.order.exception.OrderExceptionCode;

import java.util.Arrays;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Getter
public enum OrderStatus {
	WAITING("대기중"),
	ACCEPTED("조리중"),
	DONE("완료"),
	CANCELED("취소");

	private final String displayName;

	OrderStatus(String displayName) {
		this.displayName = displayName;
	}

	 public static OrderStatus of(String status) {
	 	return Arrays.stream(OrderStatus.values())
	 		.filter(s -> s.name().equalsIgnoreCase(status))
	 		.findFirst()
			.orElseThrow(() -> new OrderException(OrderExceptionCode.INVALID_ORDER_ID));
	 }

	public boolean canTransitionTo(OrderStatus nextStatus) {
        return switch (this) {
            case WAITING -> nextStatus == ACCEPTED || nextStatus == CANCELED;
            case ACCEPTED -> nextStatus == DONE || nextStatus == CANCELED;
            case DONE -> false; // 완료된 주문은 상태 변경 불가
            case CANCELED -> false; // 취소된 주문도 변경 불가
            default -> false;
        };
	}
}
