package rider.nbc.domain.store.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rider.nbc.domain.keyword.service.KeywordService;
import rider.nbc.domain.store.dto.PageResponseDto;
import rider.nbc.domain.store.dto.StoreCreateRequestDto;
import rider.nbc.domain.store.dto.StoreDetailResponseDto;
import rider.nbc.domain.store.dto.StoreResponseDto;
import rider.nbc.domain.store.dto.StoreReviewsResponseDto;
import rider.nbc.domain.store.dto.StoreSearchResponseDto;
import rider.nbc.domain.store.dto.StoreUpdateRequestDto;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.service.StoreService;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.response.CommonResponse;

/**
 * @author : kimjungmin
 * Created on : 2025. 4. 22.
 */
@RestController
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;
	private final KeywordService keywordService;

	/**
	 * 가게를 생성
	 * 유저의 ROLE이 CEO일 경우에만 가게 생성이 가능 해당 유저가 가게를 3개 미만으로 생성했을 때만 생성 가능
	 *
	 * @param requestDto 가게 생성 요청 DTO
	 * @return 생성된 가게 정보
	 */
	@PostMapping("/api/v1/stores")
	public ResponseEntity<CommonResponse<StoreResponseDto>> createStore(
		@Valid @RequestBody StoreCreateRequestDto requestDto,
		@AuthenticationPrincipal AuthUser authUser) {
		Store savedStore = storeService.createStore(authUser.getId(), requestDto);
		CompletableFuture.runAsync(() -> keywordService.insertKeyword(savedStore));

		StoreResponseDto responseDto = StoreResponseDto.fromEntity(savedStore);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(CommonResponse.<StoreResponseDto>builder()
				.success(true)
				.status(HttpStatus.CREATED.value())
				.message("가게가 성공적으로 생성되었습니다.")
				.result(responseDto)
				.build());
	}

	/**
	 * 가게 정보 업데이트
	 * 가게 소유자만 업데이트 가능
	 *
	 * @param storeId    업데이트할 가게 ID
	 * @param requestDto 업데이트 요청 DTO
	 * @param authUser   인증된 사용자 정보
	 * @return 업데이트된 가게 정보
	 */
	@PutMapping("/api/v1/stores/{storeId}")
	public ResponseEntity<CommonResponse<StoreResponseDto>> updateStore(
		@PathVariable Long storeId,
		@RequestBody StoreUpdateRequestDto requestDto,
		@AuthenticationPrincipal AuthUser authUser) {
		Store updatedStore = storeService.updateStore(storeId, authUser.getId(), requestDto);
		CompletableFuture.runAsync(() -> keywordService.insertKeyword(updatedStore));
		StoreResponseDto responseDto = StoreResponseDto.fromEntity(updatedStore);

		return ResponseEntity.ok()
			.body(CommonResponse.<StoreResponseDto>builder()
				.success(true)
				.status(HttpStatus.OK.value())
				.message("가게가 성공적으로 업데이트되었습니다.")
				.result(responseDto)
				.build());
	}

	/**
	 * 가게 단건 조회
	 * 메뉴 정보를 포함한 가게 정보를 조회
	 *
	 * @param storeId 조회할 가게 ID
	 * @return 조회된 가게 정보
	 */
	@GetMapping("/api/v1/stores/{storeId}")
	public ResponseEntity<CommonResponse<StoreDetailResponseDto>> getStore(@PathVariable Long storeId) {
		Store store = storeService.getStoreWithMenus(storeId);
		StoreDetailResponseDto responseDto = StoreDetailResponseDto.fromEntity(store);

		return ResponseEntity.ok()
			.body(CommonResponse.<StoreDetailResponseDto>builder()
				.success(true)
				.status(HttpStatus.OK.value())
				.message("가게 조회가 성공적으로 완료되었습니다.")
				.result(responseDto)
				.build());
	}

	@GetMapping("/api/v1/stores/{storeId}/reviews")
	public ResponseEntity<CommonResponse<StoreReviewsResponseDto>> getStoreWithMenus(@PathVariable Long storeId) {
		StoreReviewsResponseDto responseDto = storeService.getStoreReviews(storeId);

		return ResponseEntity.ok(CommonResponse
			.of(true, HttpStatus.OK.value(), "가게 리뷰들 조회 성공", responseDto));
	}

	/**
	 * 가게 삭제 (폐업 처리)
	 * 가게 소유자만 삭제 가능
	 *
	 * @param storeId  삭제할 가게 ID
	 * @param authUser 인증된 사용자 정보
	 * @return 삭제 성공 응답
	 */
	@DeleteMapping("/api/v1/stores/{storeId}")
	public ResponseEntity<CommonResponse<Void>> deleteStore(
		@PathVariable Long storeId,
		@AuthenticationPrincipal AuthUser authUser) {

		storeService.deleteStore(storeId, authUser.getId());

		return ResponseEntity.ok()
			.body(CommonResponse.<Void>builder()
				.success(true)
				.status(HttpStatus.OK.value())
				.message("가게가 성공적으로 폐업 처리되었습니다.")
				.build());
	}

	/**
	 * 키워드로 가게 검색
	 *
	 * @param keyword 검색할 키워드
	 * @param page 페이지 번호 (0부터 시작)
	 * @param size 페이지 크기
	 * @return 키워드와 관련된 가게 목록 (id, name, storePicture)과 페이지 정보
	 */
	@GetMapping("/api/v1/stores/search")
	public ResponseEntity<CommonResponse<PageResponseDto<StoreSearchResponseDto>>> searchStores(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		PageResponseDto<StoreSearchResponseDto> pageResponse = storeService.searchStoresByKeyword(keyword, page, size);

		return ResponseEntity.ok()
			.body(CommonResponse.<PageResponseDto<StoreSearchResponseDto>>builder()
				.success(true)
				.status(HttpStatus.OK.value())
				.message("가게 검색이 성공적으로 완료되었습니다.")
				.result(pageResponse)
				.build());
	}
}
