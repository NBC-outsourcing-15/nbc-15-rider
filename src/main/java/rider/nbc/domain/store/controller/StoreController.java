package rider.nbc.domain.store.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rider.nbc.domain.store.dto.StoreCreateRequestDto;
import rider.nbc.domain.store.dto.StoreResponseDto;
import rider.nbc.domain.store.dto.StoreUpdateRequestDto;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.service.StoreService;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.response.CommonResponse;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@RestController
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

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
		// 현재 인증된 사용자 정보 가져오기
		Store savedStore = storeService.createStore(authUser.getId(), requestDto);
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
	 * @param storeId 업데이트할 가게 ID
	 * @param requestDto 업데이트 요청 DTO
	 * @param authUser 인증된 사용자 정보
	 * @return 업데이트된 가게 정보
	 */
	@PutMapping("/api/v1/stores/{storeId}")
	public ResponseEntity<CommonResponse<StoreResponseDto>> updateStore(
		@PathVariable Long storeId,
		@RequestBody StoreUpdateRequestDto requestDto,
		@AuthenticationPrincipal AuthUser authUser) {

		Store updatedStore = storeService.updateStore(storeId, authUser.getId(), requestDto);
		StoreResponseDto responseDto = StoreResponseDto.fromEntity(updatedStore);

		return ResponseEntity.ok()
			.body(CommonResponse.<StoreResponseDto>builder()
				.success(true)
				.status(HttpStatus.OK.value())
				.message("가게가 성공적으로 업데이트되었습니다.")
				.result(responseDto)
				.build());
	}
}
