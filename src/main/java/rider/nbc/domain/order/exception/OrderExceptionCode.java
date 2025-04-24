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

    INVALID_ORDER_ID(false, HttpStatus.NOT_FOUND, "유효한 주문 번호가 아닙니다."),
    INVALID_STATUS(false,HttpStatus.BAD_REQUEST,"지원하지 않는 상태입니다."),

    NOT_OWNER(false, HttpStatus.UNAUTHORIZED, "해당 가게 사장님만 주문 상태를 변경할 수 있어요."),
    CANT_CHANGE_STATUS(false, HttpStatus.BAD_REQUEST, "현재 상태에서 해당 상태로 변경이 불가능합니다.");


    private final boolean isSuccess;
    private final HttpStatus httpStatus;
    private final String message;
}


