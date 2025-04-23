package rider.nbc.domain.payment.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.payment.enums.OrderNameType;
import rider.nbc.domain.payment.enums.PayType;

@Getter
@AllArgsConstructor
public class PaymentRequest {
    @NotNull(message = "결제 수단을 입력해주세요.")
    @Enumerated(EnumType.STRING)
    private PayType payType;

    @NotNull(message = "금액을 입력해주세요.")
    @Min(value = 1000, message = "1000원 부터 충전이 가능합니다.")
    private Long amount;

    @NotNull(message = "주문 유형을 입력해주세요.")
    @Enumerated(EnumType.STRING)
    private OrderNameType orderName;

}