package rider.nbc.global.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtils {
    public static void logError(Exception exception) {
        log.error("exception : {}", exception.getMessage(), exception);
    }
}
