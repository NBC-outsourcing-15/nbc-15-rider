package rider.nbc.domain.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import rider.nbc.domain.payment.exception.PaymentException;
import rider.nbc.domain.payment.exception.code.PaymentExceptionCode;

import java.util.Arrays;

public enum OrderNameType {
	CHARGE;

	@JsonCreator
	public static OrderNameType forValue(String value) {
		return Arrays.stream(OrderNameType.values())
				.filter(v -> v.name().equalsIgnoreCase(value))
				.findFirst()
				.orElseThrow(() -> new PaymentException(PaymentExceptionCode.INVALID_ORDER_TYPE));
	}
}
