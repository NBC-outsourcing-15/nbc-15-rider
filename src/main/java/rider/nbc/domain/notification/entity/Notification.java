package rider.nbc.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import rider.nbc.domain.notification.exception.NotificationException;
import rider.nbc.domain.notification.exception.NotificationExceptionCode;
import rider.nbc.domain.order.enums.OrderStatus;
import rider.nbc.global.config.TimeBaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    public static Notification create(Long userId, Long orderId, OrderStatus status, String message) {
        return Notification.builder()
                .userId(userId)
                .orderId(orderId)
                .orderStatus(status)
                .message(message)
                .isRead(false)
                .build();
    }

    public void update(OrderStatus status, String message) {
        this.orderStatus = status;
        this.message = message;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void validateIsNotRead() {
        if (isRead) {
            throw new NotificationException(NotificationExceptionCode.ALREADY_READ);
        }
    }

    public void validateStatusTransition(OrderStatus newStatus) {
        if (this.orderStatus == OrderStatus.CANCELED || this.orderStatus == OrderStatus.DONE) {
            throw new NotificationException(NotificationExceptionCode.STATUS_CHANGE_FORBIDDEN);
        }
    }

}
