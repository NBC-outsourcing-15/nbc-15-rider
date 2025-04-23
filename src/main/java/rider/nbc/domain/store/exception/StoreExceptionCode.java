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
	NOT_FOUND_STORE(false, HttpStatus.BAD_REQUEST, "가게 정보를 찾을 수 없습니다."),

	// 주인 관련
	OWNER_NOT_FOUND(false, HttpStatus.BAD_REQUEST, "사장 정보를 찾을 수 없습니다."),
	NOT_STORE_OWNER(false, HttpStatus.FORBIDDEN, "가게 주인만 수행 할 수 있는 기능입니다."),

	// 메뉴 관련
	NOT_CONTAINS_MENU(false, HttpStatus.BAD_REQUEST, "가게에 해당하는 메뉴가 존재하지 않습니다."),
	;

	private final boolean isSuccess;
	private final HttpStatus httpStatus;
	private final String message;
}