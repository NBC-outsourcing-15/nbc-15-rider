package rider.nbc.domain.menu.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import rider.nbc.domain.menu.dto.MenuCreateRequestDto;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.menu.exception.MenuException;
import rider.nbc.domain.menu.exception.MenuExceptionCode;
import rider.nbc.domain.menu.repository.MenuRepository;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.repository.StoreRepository;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.repository.UserRepository;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuRepository menuRepository;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;

	/**
	 * 메뉴를 생성
	 * 요청한 사용자가 가게의 소유자인 경우에만 메뉴 생성이 가능
	 *
	 * @param userId 요청한 사용자 ID
	 * @param storeId 메뉴를 생성할 가게 ID
	 * @param requestDto 메뉴 생성 요청 DTO
	 * @return 생성된 메뉴 정보
	 */
	@Transactional
	public Menu createMenu(String userId, Long storeId, MenuCreateRequestDto requestDto) {
		// 사용자 정보 조회
		User user = userRepository.findByOwnerIdOrThrow(Long.parseLong(userId));

		// 가게 정보 조회
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new MenuException(MenuExceptionCode.STORE_NOT_FOUND));

		// 가게 소유자 확인
		if (!store.getOwner().getId().equals(user.getId())) {
			throw new MenuException(MenuExceptionCode.NOT_STORE_OWNER);
		}

		// 메뉴 생성 및 저장
		return menuRepository.save(requestDto.toEntity(store));
	}
}