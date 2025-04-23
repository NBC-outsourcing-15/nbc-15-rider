package rider.nbc.domain.store.exception;

import org.springframework.http.HttpStatus;

import rider.nbc.global.exception.BaseException;

public class StoreException extends BaseException {

	private final StoreExceptionCode storeExceptionCode;

	public StoreException(StoreExceptionCode storeExceptionCode) {
		this.storeExceptionCode = storeExceptionCode;
	}

	@Override
	public Enum<?> getErrorCode() {
		return storeExceptionCode;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return storeExceptionCode.getHttpStatus();
	}

	@Override
	public String getMessage() {
		return storeExceptionCode.getMessage();
	}
}
