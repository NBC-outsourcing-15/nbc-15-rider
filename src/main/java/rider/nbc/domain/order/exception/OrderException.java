package rider.nbc.domain.order.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import rider.nbc.global.exception.BaseException;

@Getter
public class OrderException extends BaseException {
    private final OrderExceptionCode errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    public OrderException(OrderExceptionCode errorCode) {
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }
}
