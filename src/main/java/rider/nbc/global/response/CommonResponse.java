package rider.nbc.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
	private boolean success;
	private int status;
	private String message;
	private T result;

	public static <T> CommonResponse<T> of(boolean success, int status, String message) {
		return CommonResponse.<T>builder()
			.success(success)
			.status(status)
			.message(message)
			.build();
	}

}
