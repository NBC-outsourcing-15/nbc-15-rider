package rider.nbc.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rider.nbc.domain.review.entity.StoreReview;

public interface StoreReviewRepository extends JpaRepository<StoreReview, Long> {
    boolean existsByOrder_Id(Long orderId);
}
