package rider.nbc.domain.store.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import rider.nbc.global.exception.BaseException;

@Getter
public class StoreException extends BaseException {

	private final StoreExceptionCode errorCode;
	private final HttpStatus httpStatus;
	private final String message;

	public StoreException(StoreExceptionCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getHttpStatus();
		this.message = errorCode.getMessage();
	}
}
