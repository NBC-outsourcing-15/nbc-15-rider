package rider.nbc.domain.store.dto;

import lombok.Builder;
import lombok.Getter;
import rider.nbc.domain.review.dto.response.StoreReviewResponse;
import rider.nbc.domain.store.entity.Store;

import java.util.List;

@Getter
@Builder
public class StoreReviewsResponseDto {
    private Long storeId;
    private List<StoreReviewResponse> storeReviews;

    public static StoreReviewsResponseDto fromEntity(Store store) {
        return StoreReviewsResponseDto.builder()
                .storeId(store.getId())
                .storeReviews(store.getStoreReviews().stream()
                        .map(StoreReviewResponse::from)
                        .toList())
                .build();
    }
}
