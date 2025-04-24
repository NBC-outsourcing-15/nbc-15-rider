package rider.nbc.domain.cart.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import rider.nbc.global.exception.BaseException;

@Getter
public class CartException extends BaseException {
    private final CartExceptionCode errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    public CartException(CartExceptionCode errorCode) {
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }
}
