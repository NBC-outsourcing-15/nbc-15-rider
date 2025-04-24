package rider.nbc.domain.review.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StoreReviewCreateRequest {
    @NotBlank(message = "리뷰를 입력해주세요.")
    @Size(max = 100, message = "리뷰는 최대 100자까지 입력할 수 있습니다.")
    private String content;

    @NotNull(message = "별점을 선택해 주세요.")
    @Min(value = 0,message = "별점은 0 이상이어야 합니다.")
    @Max(value = 5,message = "별점은 5 이하이어야 합니다.")
    private int rating;

    private List<MenuReviewRequest> menuReviews;

    @Getter
    @AllArgsConstructor
    public static class MenuReviewRequest {
        private String orderMenuName;

        private String content;
    }
}
