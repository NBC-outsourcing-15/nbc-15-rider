package rider.nbc.domain.payment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentFailResponse {
	String errorCode;
	String errorMsg;
	String orderId;
}
