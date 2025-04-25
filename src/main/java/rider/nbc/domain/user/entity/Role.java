package rider.nbc.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import rider.nbc.domain.user.exception.UserException;
import rider.nbc.domain.user.exception.UserExceptionCode;

import java.util.Arrays;

public enum Role {
    ROLE_USER,
    ROLE_CEO;

    @JsonCreator
    public static Role forValue(String value) {
        return Arrays.stream(Role.values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new UserException(UserExceptionCode.INVALID_ROLE));
    }
}
