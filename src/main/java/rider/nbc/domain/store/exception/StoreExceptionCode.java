package rider.nbc.domain.store.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreExceptionCode {

	// 가게 관련
	NOT_CEO(false, HttpStatus.FORBIDDEN, "CEO만 수행할 수 있습니다."),
	TOO_MANY_STORE(false, HttpStatus.BAD_REQUEST, "가게는 최대 3개 입니다."),

	// 주인 관련
	OWNER_NOT_FOUND(false, HttpStatus.BAD_REQUEST, "사장 정보를 찾을 수 없습니다."),
	;

	private final boolean isSuccess;
	private final HttpStatus httpStatus;
	private final String message;
}