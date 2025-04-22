package rider.nbc.domain.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserExceptionCode {

    // 회원 조회 관련
    USER_NOT_FOUND(false, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_DELETED(false, HttpStatus.FORBIDDEN, "탈퇴한 사용자입니다."),
    USER_INACTIVE(false, HttpStatus.FORBIDDEN, "비활성화된 계정입니다."),

    // 회원 가입 중복
    ALREADY_REGISTERED(false, HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    EMAIL_DUPLICATION(false, HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    NICKNAME_DUPLICATION(false, HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    PHONE_DUPLICATION(false, HttpStatus.CONFLICT, "이미 사용 중인 전화번호입니다."),

    // 로그인/인증/인가 관련
    INVALID_PASSWORD(false, HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    SOCIAL_LOGIN_ONLY(false, HttpStatus.BAD_REQUEST, "소셜 로그인 전용 계정입니다."),
    NOT_NORMAL_USER(false, HttpStatus.BAD_REQUEST, "일반 로그인 사용자가 아닙니다."),

    // JWT/토큰 관련
    INVALID_TOKEN(false, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(false, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_NOT_MATCHED(false, HttpStatus.UNAUTHORIZED, "토큰 정보가 일치하지 않습니다."),
    REISSUE_FORBIDDEN(false, HttpStatus.UNAUTHORIZED, "토큰 재발급 권한이 없습니다.");

    private final boolean isSuccess;
    private final HttpStatus httpStatus;
    private final String message;
}

