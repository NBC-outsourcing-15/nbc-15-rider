package rider.nbc.domain.notification.mapper;

import rider.nbc.domain.order.enums.OrderStatus;

public class OrderStatusMessageMapper {

    public static String getMessage(OrderStatus status) {
        return switch (status) {
            case WAITING -> "주문이 접수되었습니다.";
            case ACCEPTED -> "음식이 조리 중입니다.";
            case DONE -> "배달이 완료되었습니다.";
            case CANCELED -> "주문이 취소되었습니다.";
        };
    }
}
