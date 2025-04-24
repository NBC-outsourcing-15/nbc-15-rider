package rider.nbc.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rider.nbc.domain.cart.dto.response.CartListResponseDto;
import rider.nbc.domain.order.dto.responseDto.OrderResponseDto;
import rider.nbc.domain.order.service.OrderService;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.response.CommonResponse;

/**
 * @author    : kimjungmin
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
            ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<OrderResponseDto>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("장바구니 조회")
                        .result(orderService.createOrder(authUser.getId()))
                        .build());
    }

}
