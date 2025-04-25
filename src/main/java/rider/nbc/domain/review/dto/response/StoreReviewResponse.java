package rider.nbc.domain.review.dto.response;

import lombok.Builder;
import lombok.Getter;
import rider.nbc.domain.review.entity.StoreReview;
import rider.nbc.domain.review.vo.MenuReview;

import java.util.Set;

@Getter
@Builder(toBuilder = true)
public class StoreReviewResponse {
    private Long reviewId;
    private String content;
    private int rating;
    private Long orderId;
    private Long reviewerId;
    private Long storeId;
    private Set<MenuReview> menuReviews;

    public static StoreReviewResponse from(StoreReview storeReview) {
        return StoreReviewResponse.builder()
                .reviewId(storeReview.getId())
                .content(storeReview.getContent())
                .rating(storeReview.getRating())
                .orderId(storeReview.getOrderId())
                .reviewerId(storeReview.getReviewerId())
                .storeId(storeReview.getStoreId())
                .menuReviews(storeReview.getMenuReviews())
                .build();
    }
}
