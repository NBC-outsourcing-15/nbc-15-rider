package rider.nbc.domain.review.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreReviewUpdateRequest {
    @Size(max = 100, message = "리뷰는 최대 100자까지 입력할 수 있습니다.")
    private String content;

    @Min(value = 0,message = "별점은 0 이상이어야 합니다.")
    @Max(value = 5,message = "별점은 5 이하이어야 합니다.")
    private Integer rating;
}
