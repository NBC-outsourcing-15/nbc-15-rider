package rider.nbc.domain.store.service;

import static rider.nbc.domain.store.constant.StoreConstants.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import rider.nbc.domain.store.dto.StoreCreateRequestDto;
import rider.nbc.domain.store.dto.StoreUpdateRequestDto;
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
public class StoreService {

	private final StoreRepository storeRepository;
	private final UserRepository userRepository;

	/**
	 * 가게를 생성
	 * 유저의 ROLE이 CEO일 경우에만 가게 생성이 가능
	 * 해당 유저가 가게를 3개 미만으로 생성했을 때만 생성 가능
	 *
	 * @see  rider.nbc.domain.store.constant.StoreConstants 상수
	 * @param requestDto 가게 생성 요청 DTO
	 * @return 생성된 가게 정보
	 */
	@Transactional
	public Store createStore(Long ownerId, StoreCreateRequestDto requestDto) {
		// 사용자 정보 조회
		User user = userRepository.findByOwnerIdOrThrow(ownerId);

		// CEO 권한 확인
		if (!user.isCEO()) {
			throw new StoreException(StoreExceptionCode.NOT_CEO);
		}

		// 가게 생성 개수 확인
		long storeCount = storeRepository.countByOwner(user);
		if (storeCount >= MAX_STORE_COUNT) {
			throw new StoreException(StoreExceptionCode.TOO_MANY_STORE);
		}

		return storeRepository.save(requestDto.toEntity(user));
	}

	/**
	 * 가게 정보 업데이트
	 * 가게 소유자만 업데이트 가능
	 *
	 * @param storeId 업데이트할 가게 ID
	 * @param userId 요청한 사용자 ID
	 * @param requestDto 업데이트 요청 DTO
	 * @return 업데이트된 가게 정보
	 */
	@Transactional
	public Store updateStore(Long storeId, Long userId, StoreUpdateRequestDto requestDto) {
		// 가게 정보 조회
		Store store = storeRepository.findByIdOrElseThrow(storeId);

		// 사용자 정보 조회
		User user = userRepository.findByOwnerIdOrThrow(userId);

		// CEO 권한 확인
		if (!user.isCEO()) {
			throw new StoreException(StoreExceptionCode.NOT_CEO);
		}

		// 가게 소유자 확인
		if (!store.isOwner(user)) {
			throw new StoreException(StoreExceptionCode.NOT_STORE_OWNER);
		}

		// 가게 정보 업데이트
		store.update(requestDto);

		return store;
	}
}
