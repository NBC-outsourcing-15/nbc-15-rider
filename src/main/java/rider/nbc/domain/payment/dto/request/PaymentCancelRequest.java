package rider.nbc.domain.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentCancelRequest {
    private String paymentKey;
    private String cancelReason;
}
