package rider.nbc.domain.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.payment.entity.Payment;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
	private Long id;
	private String payType;
	private Long amount;
	private String orderId;
	private String orderName;
	private String customerEmail;
	private String customerNickName;
	private String successUrl;
	private String failUrl;
	private String payDate;
	private String paySuccessYn;
	private String paymentKey;
	private boolean isCanceled;

	public void updateSuccessAndFailUrl(String successUrl, String failUrl) {
		this.successUrl = successUrl;
		this.failUrl = failUrl;
	}

	public static PaymentResponse from(Payment payment) {
		return PaymentResponse.builder()
				.id(payment.getId())
				.payType(String.valueOf(payment.getPayType()))
				.amount(payment.getAmount())
				.orderId(payment.getOrderId())
				.orderName(String.valueOf(payment.getOrderName()))
				.customerEmail(payment.getCustomerEmail())
				.customerNickName(payment.getCustomerNickName())
				.payDate(payment.getPayDate())
				.paySuccessYn(payment.getPaySuccessYn())
				.paymentKey(payment.getPaymentKey())
				.isCanceled(payment.isCanceled())
				.build();
	}
}
