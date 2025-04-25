package rider.nbc.global.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import rider.nbc.domain.order.dto.responseDto.OrderResponseDto;
import rider.nbc.domain.order.dto.responseDto.OrderStatusResponseDto;
import rider.nbc.domain.order.enums.OrderStatus;
import rider.nbc.global.response.CommonResponse;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OrderLogAspect {

    @AfterReturning(
            pointcut = "execution(* rider.nbc.domain.order.controller.OrderController.createOrder(..))",
            returning = "response",
            argNames = "response"
    )
    public void logNewOrder(ResponseEntity<?> response) {
        if (response != null && response.getBody() instanceof CommonResponse<?> body) {
            Object result = body.getResult();
            if (result instanceof OrderResponseDto order) {
                log.info("[주문 생성] 시각: {}, 가게 ID: {}, 주문 ID: {}",
                        order.getCreatedAt(), order.getStoreId(), order.getOrderId());
            }
        }
    }

    @AfterReturning(
            pointcut = "execution(* rider.nbc.domain.order.controller.OrderController.patchOrderStatus(..))",
            returning = "response",
            argNames = "response"
    )
    public void logPatchStatusOrder(ResponseEntity<?> response) {

        if (response != null && response.getBody() instanceof CommonResponse<?> body) {
            Object result = body.getResult();
            if (result instanceof OrderStatusResponseDto order) {
                // 주문 정보에서 필요한 값 추출
                Long storeId = order.getStoreId();
                Long orderId = order.getOrderId();
                LocalDateTime createAt = order.getUpdateAt();
                OrderStatus orderStatus = order.getOrderStatus();

                // 로그 기록
                log.info("[주문 상태 변경] 시각: {}, 가게 ID: {}, 주문 ID: {}, 주문 상태 : {}",
                        createAt, storeId, orderId, orderStatus);

            }
        }
    }

}

