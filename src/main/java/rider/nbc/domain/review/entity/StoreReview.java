package rider.nbc.domain.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.order.entity.Order;
import rider.nbc.domain.review.vo.MenuReview;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.user.entity.User;
import rider.nbc.global.config.TimeBaseEntity;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "store_reviews")
public class StoreReview extends TimeBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int rating;

    private boolean isDeleted;

    @ElementCollection
    @CollectionTable(name = "menu_reviews", joinColumns = @JoinColumn(name = "store_review_id"))
    @Builder.Default
    private Set<MenuReview> menuReviews = new HashSet<>();

    @OneToOne(targetEntity = Order.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(targetEntity = Store.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    public Long getUserId() {
        return user.getId();
    }

    public Long getReviewerId() {
        return user.getId();
    }

    public Long getOrderId() {
        return order.getId();
    }

    public Long getStoreId() {
        return store.getId();
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateRating(int rating) {
        this.rating = rating;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
