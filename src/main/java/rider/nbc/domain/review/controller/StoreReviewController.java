package rider.nbc.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rider.nbc.domain.review.dto.request.StoreReviewCreateRequest;
import rider.nbc.domain.review.dto.request.StoreReviewUpdateRequest;
import rider.nbc.domain.review.dto.response.StoreReviewResponse;
import rider.nbc.domain.review.service.StoreReviewService;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.response.CommonResponse;

@RestController
@RequestMapping("/stores/orders/reviews")
@RequiredArgsConstructor
public class StoreReviewController {
    private final StoreReviewService storeReviewService;

    @PostMapping
    public ResponseEntity<CommonResponse<StoreReviewResponse>> createStoreReview(
            @Valid @RequestBody StoreReviewCreateRequest storeReviewCreateRequest,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(name = "storeId") Long storeId,
            @RequestParam(name = "orderId") Long orderId
    ) {
        StoreReviewResponse response = storeReviewService.createStoreReview(storeReviewCreateRequest, authUser
                , storeId, orderId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(true, HttpStatus.CREATED.value(), "리뷰 등록 성공", response));
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<StoreReviewResponse>> updateStoreReview(
            @Valid @RequestBody StoreReviewUpdateRequest storeReviewUpdateRequest,
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable(name = "reviewId") Long reviewId
    ){
        StoreReviewResponse response = storeReviewService.updateStoreReview(storeReviewUpdateRequest, authUser, reviewId);

        return ResponseEntity.ok(CommonResponse
                .of(true, HttpStatus.OK.value(), "리뷰 수정 성공", response));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<Long>> deleteStoreReview(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(name = "storeId") Long storeId,
            @PathVariable Long reviewId
            ){
        Long deleteStoreReviewId = storeReviewService.deleteStoreReview(authUser, storeId, reviewId);

        return ResponseEntity.ok(CommonResponse
                .of(true, HttpStatus.OK.value(), "리뷰 삭제 성공", deleteStoreReviewId));
    }
}
