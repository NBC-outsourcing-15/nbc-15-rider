package rider.nbc.domain.cart.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartExceptionCode {
    NO_CONTENTS(true, HttpStatus.NO_CONTENT, "장바구니가 비어있습니다."),

    CART_READ_FAILED(false, HttpStatus.BAD_REQUEST,"장바구니 읽어오기 실패"),
    CART_SAVE_FAILED(false,HttpStatus.INTERNAL_SERVER_ERROR, "장바구니 저장 실패"),

    INVALID_STORE_ID(false, HttpStatus.BAD_REQUEST, "같은 가게만 장바구니 목록에 담을 수 있습니다."),
    INVALID_CART_ITEM(false, HttpStatus.BAD_REQUEST, "장바구니에 담을 수 없는 메뉴입니다.");

    private final boolean isSuccess;
    private final HttpStatus httpStatus;
    private final String message;
}
