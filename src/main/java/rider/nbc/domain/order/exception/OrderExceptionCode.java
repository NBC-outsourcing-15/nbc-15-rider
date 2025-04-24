package rider.nbc.domain.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderExceptionCode {

    STORE_UNAVAILABLE(false, HttpStatus.BAD_REQUEST, "해당 가게에 지금 주문할 수 없습니다."),
    NOT_MET_ORDER_AMOUNT(false, HttpStatus.BAD_REQUEST, "최소 주문 금액을 만족하지 않습니다."),

    NOT_ENOUGH_POINT(false, HttpStatus.BAD_REQUEST, "소지한 포인트 금액이 부족합니다."),
    CART_SAVE_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR, "장바구니 저장 실패"),

    INVALID_STORE_ID(false, HttpStatus.BAD_REQUEST, "같은 가게만 장바구니 목록에 담을 수 있습니다."),
    INVALID_CART_ITEM(false, HttpStatus.BAD_REQUEST, "장바구니에 담을 수 없는 메뉴입니다.");

    private final boolean isSuccess;
    private final HttpStatus httpStatus;
    private final String message;
}


