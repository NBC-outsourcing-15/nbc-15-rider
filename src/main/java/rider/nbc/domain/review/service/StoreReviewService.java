package rider.nbc.domain.review.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import rider.nbc.domain.order.entity.Order;
import rider.nbc.domain.order.enums.OrderStatus;
import rider.nbc.domain.order.repository.OrderRepository;
import rider.nbc.domain.order.vo.OrderMenu;
import rider.nbc.domain.review.dto.request.StoreReviewCreateRequest;
import rider.nbc.domain.review.dto.request.StoreReviewUpdateRequest;
import rider.nbc.domain.review.dto.response.StoreReviewResponse;
import rider.nbc.domain.review.entity.StoreReview;
import rider.nbc.domain.review.exception.StoreReviewException;
import rider.nbc.domain.review.exception.code.StoreReviewExceptionCode;
import rider.nbc.domain.review.repository.StoreReviewRepository;
import rider.nbc.domain.review.vo.MenuReview;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.exception.StoreException;
import rider.nbc.domain.store.exception.StoreExceptionCode;
import rider.nbc.domain.store.repository.StoreRepository;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.exception.UserException;
import rider.nbc.domain.user.exception.UserExceptionCode;
import rider.nbc.domain.user.repository.UserRepository;
import rider.nbc.global.auth.AuthUser;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreReviewService {
    private final StoreReviewRepository storeReviewRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public StoreReviewResponse createStoreReview(StoreReviewCreateRequest storeReviewCreateRequest
            , AuthUser authUser, Long storeId, Long orderId) {
        if (storeReviewRepository.existsByOrder_Id(orderId)) {
            throw new StoreReviewException(StoreReviewExceptionCode.ALREADY_EXISTS_REVIEW);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(RuntimeException::new);
        if (!order.getOrderStatus().equals(OrderStatus.DONE)) {
            throw new StoreReviewException(StoreReviewExceptionCode.ORDER_NOT_DONE);
        }

        User user = order.getUser();
        if (!user.getId().equals(authUser.getId())) {
            throw new StoreReviewException(StoreReviewExceptionCode.NOT_ORDERER);
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreExceptionCode.NOT_FOUND_STORE));
        if (!order.getStore().getId().equals(store.getId())) {
            throw new StoreReviewException(StoreReviewExceptionCode.ORDER_NOT_BELONG_TO_STORE);
        }

        List<String> orderMenuNames = order.getOrderMenus().stream().map(OrderMenu::getMenuName).toList();

        for (StoreReviewCreateRequest.MenuReviewRequest menuReviewRequest:storeReviewCreateRequest.getMenuReviews()){
            if (!orderMenuNames.contains(menuReviewRequest.getOrderMenuName())){
                throw new StoreReviewException(StoreReviewExceptionCode.NOT_ORDER_MENU);
            }
        }

        Set<MenuReview> menuReviews = storeReviewCreateRequest.getMenuReviews().stream()
                .map(r -> MenuReview.builder()
                        .menuName(r.getOrderMenuName())
                        .content(r.getContent())
                        .build())
                .collect(Collectors.toSet());

        StoreReview storeReview = StoreReview.builder()
                .content(storeReviewCreateRequest.getContent())
                .rating(storeReviewCreateRequest.getRating())
                .order(order)
                .user(user)
                .store(store)
                .menuReviews(menuReviews)
                .build();

        StoreReview savedReview = storeReviewRepository.save(storeReview);

        return StoreReviewResponse.from(savedReview);
    }

    @Transactional
    public StoreReviewResponse updateStoreReview(StoreReviewUpdateRequest storeReviewUpdateRequest
            , AuthUser authUser, Long storeReviewId) {
        StoreReview storeReview = storeReviewRepository.findById(storeReviewId)
                .orElseThrow(() -> new StoreReviewException(StoreReviewExceptionCode.REVIEW_NOT_FOUND));
        if (storeReview.isDeleted()) {
            throw new StoreReviewException(StoreReviewExceptionCode.ALREADY_DELETED);
        }

        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));
        if (!storeReview.getUserId().equals(user.getId())) {
            throw new StoreReviewException(StoreReviewExceptionCode.REVIEW_AUTHOR_MISMATCH);
        }

        updateContentIfPresent(storeReview, storeReviewUpdateRequest.getContent());

        updateRatingIfPresent(storeReview, storeReviewUpdateRequest.getRating());

        return StoreReviewResponse.from(storeReview);
    }

    @Transactional
    public Long deleteStoreReview(AuthUser authUser, Long storeId, Long storeReviewId) {
        StoreReview storeReview = storeReviewRepository.findById(storeReviewId)
                .orElseThrow(() -> new StoreReviewException(StoreReviewExceptionCode.REVIEW_NOT_FOUND));
        if (storeReview.isDeleted()) {
            throw new StoreReviewException(StoreReviewExceptionCode.ALREADY_DELETED);
        }

        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));
        if (!storeReview.getUserId().equals(user.getId())) {
            throw new StoreReviewException(StoreReviewExceptionCode.REVIEW_AUTHOR_MISMATCH);
        }

        if (!storeReview.getStoreId().equals(storeId)) {
            throw new StoreReviewException(StoreReviewExceptionCode.REVIEW_NOT_BELONG_TO_STORE);
        }

        storeReview.delete();

        return storeReview.getId();
    }

    private void updateContentIfPresent(StoreReview storeReview, String content) {
        if (!StringUtils.hasText(content)) {
            return;
        }

        storeReview.updateContent(content);
    }

    private void updateRatingIfPresent(StoreReview storeReview, Integer rating) {
        if (rating == null) {
            return;
        }

        storeReview.updateRating(rating);
    }

}
