package rider.nbc.global.jwt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    private RefreshTokenService refreshTokenService;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        refreshTokenService = new RefreshTokenService(redisTemplate);
    }

    @Test
    void save_정상저장_호출확인() {
        Long userId = 1L;
        String token = "sample-token";
        Duration ttl = Duration.ofDays(7);

        refreshTokenService.save(userId, token, ttl);

        verify(valueOperations, times(1)).set("RT:" + userId, token, ttl);
    }

    @Test
    void get_정상조회_반환값확인() {
        Long userId = 1L;
        String expected = "stored-token";

        when(valueOperations.get("RT:" + userId)).thenReturn(expected);

        String result = refreshTokenService.get(userId);

        assertEquals(expected, result);
    }

    @Test
    void delete_정상삭제_호출확인() {
        Long userId = 1L;

        refreshTokenService.delete(userId);

        verify(redisTemplate, times(1)).delete("RT:" + userId);
    }

    @Test
    void exists_존재할때_true() {
        Long userId = 1L;

        when(redisTemplate.hasKey("RT:" + userId)).thenReturn(true);

        assertTrue(refreshTokenService.exists(userId));
    }

    @Test
    void exists_존재하지않을때_false() {
        Long userId = 1L;

        when(redisTemplate.hasKey("RT:" + userId)).thenReturn(false);

        assertFalse(refreshTokenService.exists(userId));
    }
}
