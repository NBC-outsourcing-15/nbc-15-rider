package rider.nbc.domain.payment.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import rider.nbc.domain.payment.exception.code.PaymentExceptionCode;
import rider.nbc.global.exception.BaseException;

@Getter
public class PaymentException extends BaseException {
	private final PaymentExceptionCode errorCode;
	private final HttpStatus httpStatus;
	private final String message;

	public PaymentException(PaymentExceptionCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getHttpStatus();
		this.message = errorCode.getMessage();
	}
}
