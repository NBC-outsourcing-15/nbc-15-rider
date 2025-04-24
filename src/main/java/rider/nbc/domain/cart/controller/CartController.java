package rider.nbc.domain.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rider.nbc.domain.cart.dto.request.CartAddRequestDto;
import rider.nbc.domain.cart.dto.request.CartUpdateRequestDto;
import rider.nbc.domain.cart.dto.response.CartItemResponseDto;
import rider.nbc.domain.cart.dto.response.CartListResponseDto;
import rider.nbc.domain.cart.service.CartService;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.response.CommonResponse;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * [Controller] 장바구니에 메뉴 추가
     *
     * @param cartAddRequestDto 메뉴 품복과 수량
     * @param authUser 로그인해야함
     * @return 추가되면 추가된 메뉴 정보 리턴
     */
    @PostMapping("/items")
    public ResponseEntity<CommonResponse<CartItemResponseDto>> addCartItem(
            @Valid @RequestBody CartAddRequestDto cartAddRequestDto,
            @AuthenticationPrincipal AuthUser authUser
            ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(true,
                        HttpStatus.CREATED.value(),
                        "메뉴 추가 성공",
                        cartService.addCartItem(authUser.getId(),cartAddRequestDto)));
    }

    /**
     * [Controller] 장바구니 메뉴 조회
     *
     * @param authUser 로그인 유저 정보
     * @return 해당 유저의 장바구니 내용 확인
     */
    @GetMapping
    public ResponseEntity<CommonResponse<CartListResponseDto>> getCartList(
            @AuthenticationPrincipal AuthUser authUser
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(true,
                        HttpStatus.OK.value(),
                        "장바구니 조회",
                        cartService.getCartList(authUser.getId())));
    }

    @PutMapping
    public ResponseEntity<CommonResponse<CartListResponseDto>> updateCart(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid@RequestBody CartUpdateRequestDto requestDto
            ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(true,
                        HttpStatus.OK.value(),
                        "장바구니 수정완료",
                        cartService.updateCart(authUser.getId(), requestDto)));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CommonResponse<CartListResponseDto>> deleteSelectedMenu(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long itemId
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(true,
                        HttpStatus.OK.value(),
                        "장바구니 수정완료",
                        cartService.deleteSelectedMenu(authUser.getId(), itemId)));
    }

    @DeleteMapping
    public ResponseEntity<CommonResponse<Void>> deleteCart(
            @AuthenticationPrincipal AuthUser authUser
    ){
        cartService.deleteCart(authUser.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(true,
                        HttpStatus.OK.value(),
                        "장바구니 초기화"));
    }

}
