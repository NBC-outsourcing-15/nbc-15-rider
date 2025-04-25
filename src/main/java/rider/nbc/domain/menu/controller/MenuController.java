package rider.nbc.domain.menu.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rider.nbc.domain.keyword.service.KeywordService;
import rider.nbc.domain.menu.dto.MenuCreateRequestDto;
import rider.nbc.domain.menu.dto.MenuResponseDto;
import rider.nbc.domain.menu.dto.MenuUpdateRequestDto;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.menu.service.MenuService;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.response.CommonResponse;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@RestController
@RequiredArgsConstructor
public class MenuController {

	private final MenuService menuService;
	private final KeywordService keywordService;

	/**
	 * 메뉴를 생성
	 * 요청한 사용자가 가게의 소유자인 경우에만 메뉴 생성이 가능
	 *
	 * @param storeId 메뉴를 생성할 가게 ID
	 * @param requestDto 메뉴 생성 요청 DTO
	 * @param authUser 인증 정보
	 * @return 생성된 메뉴 정보
	 */
	@PostMapping("/api/v1/stores/{storeId}/menus")
	public ResponseEntity<CommonResponse<MenuResponseDto>> createMenu(
		@PathVariable Long storeId,
		@Valid @RequestBody MenuCreateRequestDto requestDto,
		@AuthenticationPrincipal AuthUser authUser) {
		// 현재 인증된 사용자 정보 가져오기
		Long userId = authUser.getId();
		Menu savedMenu = menuService.createMenu(userId, storeId, requestDto);
		CompletableFuture.runAsync(() -> keywordService.insertKeyword(storeId, savedMenu));

		MenuResponseDto responseDto = MenuResponseDto.fromEntity(savedMenu);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(CommonResponse.of(
				true,
				HttpStatus.CREATED.value(),
				"메뉴가 성공적으로 생성되었습니다.",
				responseDto));
	}

	/**
	 * 메뉴를 수정
	 * 요청한 사용자가 가게의 소유자인 경우에만 메뉴 수정이 가능
	 *
	 * @param storeId 메뉴가 속한 가게 ID
	 * @param menuId 수정할 메뉴 ID
	 * @param requestDto 메뉴 수정 요청 DTO
	 * @param authUser 인증 정보
	 * @return 수정된 메뉴 정보
	 */
	@PutMapping("/api/v1/stores/{storeId}/menus/{menuId}")
	public ResponseEntity<CommonResponse<MenuResponseDto>> updateMenu(
		@PathVariable Long storeId,
		@PathVariable Long menuId,
		@Valid @RequestBody MenuUpdateRequestDto requestDto,
		@AuthenticationPrincipal AuthUser authUser) {
		Long userId = authUser.getId();
		Menu updatedMenu = menuService.updateMenu(userId, storeId, menuId, requestDto);
		CompletableFuture.runAsync(() -> keywordService.insertKeyword(storeId, updatedMenu));
		
		MenuResponseDto responseDto = MenuResponseDto.fromEntity(updatedMenu);

		return ResponseEntity.status(HttpStatus.OK)
			.body(CommonResponse.of(
				true,
				HttpStatus.OK.value(),
				"메뉴가 성공적으로 수정되었습니다.",
				responseDto));
	}

	/**
	 * 메뉴를 삭제
	 * 요청한 사용자가 가게의 소유자인 경우에만 메뉴 삭제가 가능
	 *
	 * @param storeId 메뉴가 속한 가게 ID
	 * @param menuId 삭제할 메뉴 ID
	 * @param authUser 인증 정보
	 * @return 삭제 성공 응답
	 */
	@DeleteMapping("/api/v1/stores/{storeId}/menus/{menuId}")
	public ResponseEntity<CommonResponse<Void>> deleteMenu(
		@PathVariable Long storeId,
		@PathVariable Long menuId,
		@AuthenticationPrincipal AuthUser authUser) {
		// 현재 인증된 사용자 정보 가져오기
		Long userId = authUser.getId();
		menuService.deleteMenu(userId, storeId, menuId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(CommonResponse.of(
				true,
				HttpStatus.OK.value(),
				"메뉴가 성공적으로 삭제되었습니다.",
				null));
	}
}
