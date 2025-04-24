package rider.nbc.domain.order.enums;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
public enum OrderStatus {
	WAITING, ACCEPTED, DONE, CANCELED;

	// public static OrderStatus of(String status) {
	// 	return Arrays.stream(OrderStatus.values())
	// 		.filter(s -> s.name().equalsIgnoreCase(status))
	// 		.findFirst()
	// 		.
	// }
}
