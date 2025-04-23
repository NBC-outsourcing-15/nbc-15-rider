package rider.nbc.domain.menu.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MenuExceptionCode {

    // 메뉴 관련
    MENU_NOT_FOUND(false, HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."),
    
    // 권한 관련
    NOT_STORE_OWNER(false, HttpStatus.FORBIDDEN, "가게 소유자만 메뉴를 생성할 수 있습니다."),
    
    // 가게 관련
    STORE_NOT_FOUND(false, HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다.");

    private final boolean isSuccess;
    private final HttpStatus httpStatus;
    private final String message;
}