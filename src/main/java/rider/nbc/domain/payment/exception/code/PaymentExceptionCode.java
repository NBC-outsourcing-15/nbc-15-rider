package rider.nbc.domain.payment.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentExceptionCode {
	INVALID_ORDER_TYPE(false, HttpStatus.BAD_REQUEST,"잘못된 주문 유형입니다. (CHARGE만 가능)"),
	INVALID_PAY_TYPE(false, HttpStatus.BAD_REQUEST,"잘못된 결제 유형입니다. (CARD만 가능)"),
	PAYMENT_ALREADY_CANCELED(false, HttpStatus.BAD_REQUEST,"취소된 결제 요청입니다."),
	PAYMENT_NOT_SUCCESS(false, HttpStatus.BAD_REQUEST,"결제가 완료된 요청이 아닙니다."),
	PAYMENT_REQUEST_NOT_FOUND(false, HttpStatus.BAD_REQUEST, "결제 요청을 찾을 수 없습니다."),
	PAYMENT_ERROR_ORDER_AMOUNT(false, HttpStatus.BAD_REQUEST, "결제 금액이 맞지 않습니다."),
	UNDEFINED_ERROR(false, HttpStatus.BAD_REQUEST, "정의되지 않은 오류입니다."),
	ALREADY_PROCESSED_PAYMENT(false, HttpStatus.BAD_REQUEST, "이미 처리된 결제 입니다."),
	PAYMENT_CANCEL_ERROR(false, HttpStatus.BAD_REQUEST, "결제 취소에 실패했습니다.");

	private final boolean isSuccess;
	private final HttpStatus httpStatus;
	private final String message;
}
