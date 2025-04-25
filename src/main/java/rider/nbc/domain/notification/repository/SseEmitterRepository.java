package rider.nbc.domain.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public SseEmitter save(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterMap.put(userId, emitter);

        emitter.onCompletion(() -> emitterMap.remove(userId));
        emitter.onTimeout(() -> emitterMap.remove(userId));
        emitter.onError(e -> emitterMap.remove(userId));

        return emitter;
    }

    public void send(Long userId, Object data) {
        SseEmitter emitter = emitterMap.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(data));
                System.out.println("✅ SSE 전송 완료 - userId: " + userId);
            } catch (IOException e) {
                emitterMap.remove(userId);
                emitter.completeWithError(e);
            }
        } else {
            System.out.println("⚠️ emitter 없음 - userId: " + userId);
        }
    }
}
