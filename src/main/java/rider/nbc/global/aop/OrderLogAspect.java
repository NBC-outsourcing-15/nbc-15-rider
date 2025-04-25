package rider.nbc.global.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
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

    @AfterReturning(pointcut = "execution(* rider.nbc.domain.order.controller.OrderController.createOrder(..))"
            , returning = "response")
    public void logNewOrder(CommonResponse<OrderResponseDto> response) {
        if (response != null && response.getResult() != null) {
            OrderResponseDto orderResponse = response.getResult();

            // 주문 정보에서 필요한 값 추출
            Long storeId = orderResponse.getStoreId();
            Long orderId = orderResponse.getOrderId();
            LocalDateTime createAt = orderResponse.getCreatedAt();

            // 로그 기록
            log.info("[주문 생성] 시각: {}, 가게 ID: {}, 주문 ID: {}", createAt, storeId, orderId);

        }
    }

    @AfterReturning(pointcut = "execution(* rider.nbc.domain.order.controller.OrderController.patchOrderStatus(..))"
            , returning = "response")
    public void logPatchStatusOrder(CommonResponse<OrderStatusResponseDto> response) {
        if (response != null && response.getResult() != null) {
            OrderStatusResponseDto orderResponse = response.getResult();

            // 주문 정보에서 필요한 값 추출
            Long storeId = orderResponse.getStoreId();
            Long orderId = orderResponse.getOrderId();
            LocalDateTime createAt = orderResponse.getUpdateAt();
            OrderStatus orderStatus = orderResponse.getOrderStatus();

            // 로그 기록
            log.info("[주문 상태 변경] 시각: {}, 가게 ID: {}, 주문 ID: {}, 주문 상태 : {}", createAt, storeId, orderId, orderStatus);

        }
    }

}

