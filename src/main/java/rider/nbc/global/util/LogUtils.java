package rider.nbc.global.util;

import lombok.extern.slf4j.Slf4j;
import rider.nbc.global.exception.BaseException;

@Slf4j
public class LogUtils {
    public static void logError(Throwable throwable) {
        if (throwable instanceof BaseException baseException) {
            log.error("예외 발생: {} (ErrorCode: {})", baseException.getMessage(), baseException.getMessage());
        }

        StackTraceElement firstStackTrace = throwable.getStackTrace()[0];
        log.error("발생 위치: {}:{} - Thread: {}, Method: {}",
                firstStackTrace.getClassName(), firstStackTrace.getLineNumber(),
                Thread.currentThread().getName(), firstStackTrace.getMethodName());
    }
}