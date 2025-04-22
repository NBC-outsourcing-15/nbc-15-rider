package rider.nbc.domain.order.enums;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
public enum OrderStatus {
	WAITING, COOKING, DONE;

	// public static OrderStatus of(String status) {
	// 	return Arrays.stream(OrderStatus.values())
	// 		.filter(s -> s.name().equalsIgnoreCase(status))
	// 		.findFirst()
	// 		.
	// }
}
