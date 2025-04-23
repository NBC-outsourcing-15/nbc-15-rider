package rider.nbc.domain.menu.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import rider.nbc.domain.menu.dto.MenuCreateRequestDto;
import rider.nbc.domain.menu.dto.MenuUpdateRequestDto;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.menu.exception.MenuException;
import rider.nbc.domain.menu.exception.MenuExceptionCode;
import rider.nbc.domain.menu.repository.MenuRepository;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.exception.StoreException;
import rider.nbc.domain.store.exception.StoreExceptionCode;
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
		Store store = storeRepository.findByIdOrElseThrow(storeId);

		// 가게 소유자 확인
		if (!store.isOwner(user)) {
			throw new MenuException(MenuExceptionCode.NOT_STORE_OWNER);
		}

		// 메뉴 생성 및 저장
		return menuRepository.save(requestDto.toEntity(store));
	}

	/**
	 * 메뉴를 수정
	 * 요청한 사용자가 가게의 소유자인 경우에만 메뉴 수정이 가능
	 *
	 * @param userId 요청한 사용자 ID
	 * @param storeId 메뉴가 속한 가게 ID
	 * @param menuId 수정할 메뉴 ID
	 * @param requestDto 메뉴 수정 요청 DTO
	 * @return 수정된 메뉴 정보
	 */
	@Transactional
	public Menu updateMenu(String userId, Long storeId, Long menuId, MenuUpdateRequestDto requestDto) {
		// 사용자 정보 조회
		User user = userRepository.findByOwnerIdOrThrow(Long.parseLong(userId));

		// 가게 정보 조회
		Store store = storeRepository.findStoreWithMenusByIdOrElseThrow(storeId);

		// 가게 소유자 확인
		if (!store.isOwner(user)) {
			throw new StoreException(StoreExceptionCode.NOT_STORE_OWNER);
		}

		Menu menu = menuRepository.findByIdOrElseThrow(menuId);

		// 메뉴가 해당 가게에 속하는지 확인
		if (!store.contains(menu)) {
			throw new StoreException(StoreExceptionCode.NOT_CONTAINS_MENU);
		}

		// 메뉴 정보 업데이트
		menu.update(requestDto);

		return menu;
	}
}
