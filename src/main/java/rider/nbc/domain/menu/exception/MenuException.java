package rider.nbc.domain.menu.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import rider.nbc.global.exception.BaseException;

@Getter
public class MenuException extends BaseException {

    private final MenuExceptionCode errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    public MenuException(MenuExceptionCode errorCode) {
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }
}