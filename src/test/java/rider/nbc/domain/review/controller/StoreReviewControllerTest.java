package rider.nbc.domain.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import rider.nbc.domain.review.dto.request.StoreReviewCreateRequest;
import rider.nbc.domain.review.dto.request.StoreReviewUpdateRequest;
import rider.nbc.domain.review.dto.response.StoreReviewResponse;
import rider.nbc.domain.review.service.StoreReviewService;
import rider.nbc.domain.review.vo.MenuReview;
import rider.nbc.domain.support.security.TestSecurityConfig;
import rider.nbc.domain.support.security.WithCustomMockUser;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreReviewController.class)
@Import(TestSecurityConfig.class)
class StoreReviewControllerTest {
    @MockitoBean
    private StoreReviewService storeReviewService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final StoreReviewCreateRequest storeReviewCreateRequest = new StoreReviewCreateRequest("맛있다", 4
            , List.of(new StoreReviewCreateRequest.MenuReviewRequest("test menu", "강추")));

    private final StoreReviewUpdateRequest storeReviewUpdateRequest =
            new StoreReviewUpdateRequest("맛있고 또 맛있다", 5);

    private final MenuReview menuReview = MenuReview.builder()
            .menuName("test menu")
            .content("강추")
            .build();

    private final StoreReviewResponse response = StoreReviewResponse.builder()
            .reviewId(1L)
            .content("맛있다")
            .rating(4)
            .orderId(1L)
            .reviewerId(1L)
            .storeId(1L)
            .menuReviews(Set.of(menuReview))
            .build();

    @Test
    @WithCustomMockUser
    @DisplayName("리뷰 등록 성공")
    void success_createStoreReview() throws Exception {
        // Given
        given(storeReviewService.createStoreReview(any(), any(), anyLong(), anyLong()))
                .willReturn(response);

        // When
        ResultActions perform = mockMvc.perform(post("/stores/orders/reviews")
                .param("storeId", "1")
                .param("orderId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storeReviewCreateRequest)));

        // Then
        verify(storeReviewService, times(1)).createStoreReview(any(), any(), anyLong(), anyLong());

        perform.andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.success")
                                .value(true),
                        jsonPath("$.status")
                                .value(201),
                        jsonPath("$.message")
                                .value("리뷰 등록 성공"),
                        jsonPath("$.result.reviewId")
                                .value(1L),
                        jsonPath("$.result.content")
                                .value(storeReviewCreateRequest.getContent()),
                        jsonPath("$.result.rating")
                                .value(storeReviewCreateRequest.getRating()),
                        jsonPath("$.result.orderId")
                                .value(1L),
                        jsonPath("$.result.reviewerId")
                                .value(1L),
                        jsonPath("$.result.storeId")
                                .value(1L),
                        jsonPath("$.result.menuReviews.[0].menuName")
                                .value(menuReview.getMenuName()),
                        jsonPath("$.result.menuReviews.[0].content")
                                .value(menuReview.getContent())
                );

    }

    @Test
    @WithCustomMockUser
    @DisplayName("리뷰 수정 성공")
    void success_updateStoreReview() throws Exception {
        // Given
        given(storeReviewService.updateStoreReview(any(), any(), anyLong()))
                .willReturn(response.toBuilder()
                        .content("맛있고 또 맛있다")
                        .rating(5)
                        .build());

        // When
        ResultActions perform = mockMvc.perform(patch("/stores/orders/reviews/{reviewId}", 1L)
                .param("storeId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storeReviewUpdateRequest)));

        // Then
        verify(storeReviewService, times(1)).updateStoreReview(any(), any(), anyLong());

        perform.andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.success")
                                .value(true),
                        jsonPath("$.status")
                                .value(200),
                        jsonPath("$.message")
                                .value("리뷰 수정 성공"),
                        jsonPath("$.result.reviewId")
                                .value(1L),
                        jsonPath("$.result.content")
                                .value(storeReviewUpdateRequest.getContent()),
                        jsonPath("$.result.rating")
                                .value(storeReviewUpdateRequest.getRating()),
                        jsonPath("$.result.orderId")
                                .value(1L),
                        jsonPath("$.result.reviewerId")
                                .value(1L),
                        jsonPath("$.result.storeId")
                                .value(1L),
                        jsonPath("$.result.menuReviews.[0].menuName")
                                .value(menuReview.getMenuName()),
                        jsonPath("$.result.menuReviews.[0].content")
                                .value(menuReview.getContent())
                );

    }

    @Test
    @WithCustomMockUser
    @DisplayName("리뷰 삭제 성공")
    void success_deleteStoreReview() throws Exception {
        // Given
        long storeId = 1;

        given(storeReviewService.deleteStoreReview(any(), anyLong(), anyLong()))
                .willReturn(storeId);

        // When
        ResultActions perform = mockMvc.perform(delete("/stores/orders/reviews/{reviewId}", 1L)
                .param("storeId", String.valueOf(storeId)));

        // Then
        verify(storeReviewService, times(1)).deleteStoreReview(any(), anyLong(), anyLong());

        perform.andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.success")
                                .value(true),
                        jsonPath("$.status")
                                .value(200),
                        jsonPath("$.message")
                                .value("리뷰 삭제 성공"),
                        jsonPath("$.result")
                                .value(storeId)
                );

    }

}
