package rider.nbc.domain.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rider.nbc.domain.order.dto.requestDto.OrderStatusRequestDto;
import rider.nbc.domain.order.dto.responseDto.OrderResponseDto;
import rider.nbc.domain.order.service.OrderService;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.response.CommonResponse;

/**
 * @author : kimjungmin
 * Created on : 2025. 4. 22.
 */

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * [Controller] 주문생성
     * 
     * @param authUser 로그인한 유저 Id
     * @return 그 유저가 담은 장바구니로 만든 주문내역
     */
    @PostMapping
    public ResponseEntity<CommonResponse<OrderResponseDto>> createOrder(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(true,
                        HttpStatus.CREATED.value(),
                        "주문 생성 완료",
                        orderService.createOrder(authUser.getId())));
    }

    /**
     * [Controller] 주문 변경
     * 
     * @param authUser 로그인한 유저 ID
     * @param orderId 상태 바굴 주문 Id
     * @param statusRequestDto 어떤 상태로 바꿀것인가
     * @return 성공 메시지
     */
    @PatchMapping("/{orderId}")
    public ResponseEntity<CommonResponse<String>> patchOrderStatus(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long orderId,
            @Valid@RequestBody OrderStatusRequestDto statusRequestDto
    ) {
        String result = orderService.patchOrderStatus(authUser, orderId, statusRequestDto);
        return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value()
                , "주문이" + result + "으로 변경되었습니다."));
    }
}
