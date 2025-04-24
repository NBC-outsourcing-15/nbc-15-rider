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

}
