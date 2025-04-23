package rider.nbc.domain.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rider.nbc.domain.cart.dto.request.CartAddRequestDto;
import rider.nbc.domain.cart.dto.response.CartItemResponseDto;
import rider.nbc.domain.cart.dto.response.CartListResponseDto;
import rider.nbc.domain.cart.service.CartService;
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
     * @param authentication 로그인해야함
     * @return 추가되면 추가된 메뉴 정보 리턴
     */
    @PostMapping("/items")
    public ResponseEntity<CommonResponse<CartItemResponseDto>> addCartItem(
            @Valid @RequestBody CartAddRequestDto cartAddRequestDto,
            Authentication authentication
            ){
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<CartItemResponseDto>builder()
                        .success(true)
                        .status(HttpStatus.CREATED.value())
                        .message("메뉴 추가 성공")
                        .result(cartService.addCartItem(userId,cartAddRequestDto))
                        .build());
    }
}
