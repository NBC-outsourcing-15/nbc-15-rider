package rider.nbc.domain.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import rider.nbc.global.exception.BaseException;

@Getter
public class UserException extends BaseException {
    private final UserExceptionCode errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    public UserException(UserExceptionCode errorCode) {
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }
}
