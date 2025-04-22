package rider.nbc.global.exception.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import rider.nbc.global.exception.BaseException;
import rider.nbc.global.exception.dto.ValidationError;
import rider.nbc.global.response.CommonResponse;
import rider.nbc.global.response.CommonResponses;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<CommonResponse<?>> handleBaseException(BaseException baseException) {
		// LogUtils.logError(baseException);

		return ResponseEntity.status(baseException.getHttpStatus())
			.body(CommonResponse.of(false, baseException.getHttpStatus().value(), baseException.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CommonResponses<ValidationError>> inputValidationExceptionHandler(BindingResult result) {
		// log.error(result.getFieldErrors().toString());

		List<ValidationError> validationErrors = result.getFieldErrors().stream()
			.map(fieldError -> ValidationError.builder()
				.field(fieldError.getField())
				.message(fieldError.getDefaultMessage())
				.code(fieldError.getCode())
				.build())
			.toList();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponses.of(false, HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다.",
				validationErrors));
	}
}
