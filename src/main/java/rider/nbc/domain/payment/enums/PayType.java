package rider.nbc.domain.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import rider.nbc.domain.payment.exception.PaymentException;
import rider.nbc.domain.payment.exception.code.PaymentExceptionCode;

import java.util.Arrays;

public enum PayType {
	CARD;

	@JsonCreator
	public static PayType forValue(String value) {
		return Arrays.stream(PayType.values())
				.filter(v -> v.name().equalsIgnoreCase(value))
				.findFirst()
				.orElseThrow(() -> new PaymentException(PaymentExceptionCode.INVALID_PAY_TYPE));
	}
}
