package rider.nbc.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rider.nbc.domain.notification.dto.NotificationResponseDto;
import rider.nbc.domain.notification.entity.Notification;
import rider.nbc.domain.notification.mapper.OrderStatusMessageMapper;
import rider.nbc.domain.notification.repository.NotificationRepository;
import rider.nbc.domain.notification.repository.SseEmitterRepository;
import rider.nbc.domain.order.enums.OrderStatus;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SseEmitterRepository sseEmitterRepository;

    @Transactional
    public void sendOrderStatusNotification(Long userId, Long orderId, OrderStatus status) {
        // 메시지 생성
        String message = OrderStatusMessageMapper.getMessage(status);

        // 알림 저장 or 수정
        Notification notification = notificationRepository.findByUserIdAndOrderId(userId, orderId)
                .orElse(Notification.create(userId, orderId, status, message));

        notification.update(status, message);
        notificationRepository.save(notification);

        NotificationResponseDto responseDto = NotificationResponseDto.from(notification);
        sseEmitterRepository.send(userId, responseDto);
    }
}
