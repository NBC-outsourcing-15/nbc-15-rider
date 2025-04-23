package rider.nbc.domain.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rider.nbc.domain.cart.dto.request.CartAddRequestDto;
import rider.nbc.domain.cart.dto.request.CartUpdateRequestDto;
import rider.nbc.domain.cart.dto.response.CartItemResponseDto;
import rider.nbc.domain.cart.dto.response.CartListResponseDto;
import rider.nbc.domain.cart.service.CartService;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.response.CommonResponse;
import rider.nbc.global.response.CommonResponses;

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
                .body(CommonResponse.<CartItemResponseDto>builder()
                        .success(true)
                        .status(HttpStatus.CREATED.value())
                        .message("메뉴 추가 성공")
                        .result(cartService.addCartItem(authUser.getId(),cartAddRequestDto))
                        .build());
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
                .body(CommonResponse.<CartListResponseDto>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("장바구니 조회")
                        .result(cartService.getCartList(authUser.getId()))
                        .build());
    }

    @PutMapping
    public ResponseEntity<CommonResponse<CartListResponseDto>> updateCart(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid@RequestBody CartUpdateRequestDto requestDto
            ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<CartListResponseDto>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("장바구니 조회")
                        .result(cartService.updateCart(authUser.getId(), requestDto))
                        .build());
    }

}
