package rider.nbc.domain.notification.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationExceptionCode {

    INVALID_STATUS(false, HttpStatus.BAD_REQUEST, "지원하지 않는 상태입니다."),
    STATUS_CHANGE_FORBIDDEN(false, HttpStatus.FORBIDDEN, "해당 상태로 변경할 수 없습니다."),
    ALREADY_READ(false, HttpStatus.BAD_REQUEST, "이미 읽은 알림입니다."),
    NOT_FOUND(false, HttpStatus.NOT_FOUND, "해당 알림을 찾을 수 없습니다.");

    private final boolean isSuccess;
    private final HttpStatus httpStatus;
    private final String message;
}
