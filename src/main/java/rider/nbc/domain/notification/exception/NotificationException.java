package rider.nbc.domain.notification.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import rider.nbc.global.exception.BaseException;

@Getter
public class NotificationException extends BaseException {
    private final NotificationExceptionCode errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    public NotificationException(NotificationExceptionCode errorCode) {
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }
}
