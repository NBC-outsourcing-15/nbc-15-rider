package rider.nbc.domain.review.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StoreReviewExceptionCode {
    REVIEW_NOT_FOUND(false, HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    ALREADY_EXISTS_REVIEW(false, HttpStatus.CONFLICT, "해당 주문에 대한 리뷰가 존재합니다."),
    ALREADY_DELETED(false, HttpStatus.BAD_REQUEST, "이미 삭제된 리뷰입니다."),
    REVIEW_AUTHOR_MISMATCH(false, HttpStatus.FORBIDDEN, "유저가 작성한 리뷰가 아닙니다."),
    REVIEW_NOT_BELONG_TO_STORE(false, HttpStatus.BAD_REQUEST, "해당 가게에 등록된 리뷰가 아닙니다."),
    ORDER_NOT_DONE(false, HttpStatus.BAD_REQUEST, "주문이 완료되지 않았습니다."),
    NOT_ORDERER(false, HttpStatus.FORBIDDEN, "주문한 유저가 아닙니다."),
    ORDER_NOT_BELONG_TO_STORE(false, HttpStatus.BAD_REQUEST, "해당 가게의 주문이 아닙니다."),
    NOT_ORDER_MENU(false, HttpStatus.BAD_REQUEST, "주문한 메뉴가 아닙니다."),
    ;

    private final boolean isSuccess;
    private final HttpStatus status;
    private final String message;
}
