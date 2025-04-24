package rider.nbc.domain.review.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import rider.nbc.domain.review.exception.code.StoreReviewExceptionCode;
import rider.nbc.global.exception.BaseException;

@Getter
public class StoreReviewException extends BaseException {
    private final StoreReviewExceptionCode errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    public StoreReviewException(StoreReviewExceptionCode errorCode) {
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }
}
