package rider.nbc.domain.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rider.nbc.domain.cart.dto.response.CartListResponseDto;
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

    @PatchMapping("/{orderId}")
    public ResponseEntity<CommonResponse<String>> patchOrderStatus(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long orderId,
            @Valid@RequestBody OrderStatusRequestDto statusRequestDto
    ) {
        orderService.patchOrderStatus(authUser, orderId);
        return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value()
                , "주문의 상태가 변경되었습니다." + statusRequestDto.getOrderStatus()));
    }
}
