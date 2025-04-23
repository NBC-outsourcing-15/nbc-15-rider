package rider.nbc.domain.menu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rider.nbc.domain.menu.dto.MenuCreateRequestDto;
import rider.nbc.domain.menu.dto.MenuResponseDto;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.menu.service.MenuService;
import rider.nbc.global.response.CommonResponse;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 메뉴를 생성
     * 요청한 사용자가 가게의 소유자인 경우에만 메뉴 생성이 가능
     *
     * @param storeId 메뉴를 생성할 가게 ID
     * @param requestDto 메뉴 생성 요청 DTO
     * @param authentication 인증 정보
     * @return 생성된 메뉴 정보
     */
    @PostMapping("/api/v1/stores/{storeId}/menus")
    public ResponseEntity<CommonResponse<MenuResponseDto>> createMenu(
            @PathVariable Long storeId,
            @Valid @RequestBody MenuCreateRequestDto requestDto,
            Authentication authentication) {
        // 현재 인증된 사용자 정보 가져오기
        String userId = authentication.getName();
        Menu savedMenu = menuService.createMenu(userId, storeId, requestDto);
        MenuResponseDto responseDto = MenuResponseDto.fromEntity(savedMenu);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<MenuResponseDto>builder()
                        .success(true)
                        .status(HttpStatus.CREATED.value())
                        .message("메뉴가 성공적으로 생성되었습니다.")
                        .result(responseDto)
                        .build());
    }
}