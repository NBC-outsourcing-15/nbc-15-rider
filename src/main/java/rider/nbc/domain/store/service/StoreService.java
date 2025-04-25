package rider.nbc.domain.store.service;

import static rider.nbc.domain.store.constant.StoreConstants.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import rider.nbc.domain.keyword.repository.KeywordRepository;
import rider.nbc.domain.store.dto.PageResponseDto;
import rider.nbc.domain.store.dto.StoreCreateRequestDto;
import rider.nbc.domain.store.dto.StoreReviewsResponseDto;
import rider.nbc.domain.store.dto.StoreSearchResponseDto;
import rider.nbc.domain.store.dto.StoreUpdateRequestDto;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.exception.StoreException;
import rider.nbc.domain.store.exception.StoreExceptionCode;
import rider.nbc.domain.store.repository.StoreRepository;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.repository.UserRepository;

/**
 * @author : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final UserRepository userRepository;
	private final KeywordRepository keywordRepository;

	/**
	 * 가게를 생성
	 * 유저의 ROLE이 CEO일 경우에만 가게 생성이 가능
	 * 해당 유저가 가게를 3개 미만으로 생성했을 때만 생성 가능
	 *
	 * @param requestDto 가게 생성 요청 DTO
	 * @return 생성된 가게 정보
	 * @see rider.nbc.domain.store.constant.StoreConstants 상수
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
	 * @param storeId    업데이트할 가게 ID
	 * @param userId     요청한 사용자 ID
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

	/**
	 * 가게 단건 조회
	 * 메뉴 정보를 포함한 가게 정보를 조회
	 *
	 * @param storeId 조회할 가게 ID
	 * @return 조회된 가게 정보
	 */
	@Transactional(readOnly = true)
	public Store getStoreWithMenus(Long storeId) {
		return storeRepository.findStoreWithMenusByIdOrElseThrow(storeId);
	}

	/**
	 * 가게 단건 조회</br>
	 * 가게에 달린 리뷰들과 리뷰에 있는 메뉴 정보들을 조회하여 반환
	 *
	 * @param storeId 가게 id
	 * @return 가게 리뷰들
	 * @author 이승현
	 */
	public StoreReviewsResponseDto getStoreReviews(Long storeId) {
		Store store = storeRepository.findStoreWithStoreReviewsById(storeId)
			.orElseThrow(() -> new StoreException(StoreExceptionCode.NOT_FOUND_STORE));

		return StoreReviewsResponseDto.fromEntity(store);
	}

	/**
	 * 가게 삭제 (폐업 처리)
	 * 가게 소유자만 삭제 가능
	 *
	 * @param storeId 삭제할 가게 ID
	 * @param userId  요청한 사용자 ID
	 */
	@Transactional
	public void deleteStore(Long storeId, Long userId) {
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

		storeRepository.delete(store);
	}

	/**
	 * 키워드로 가게 검색 (페이징)
	 *
	 * @param keyword 검색할 키워드
	 * @param page 페이지 번호 (0부터 시작)
	 * @param size 페이지 크기
	 * @return 키워드와 관련된 가게 목록 (id, name, storePicture)과 페이지 정보
	 */
	@Transactional(readOnly = true)
	public PageResponseDto<StoreSearchResponseDto> searchStoresByKeyword(String keyword, int page, int size) {
		long totalElements = keywordRepository.countStoresByKeyword(keyword);
		List<Store> stores = keywordRepository.searchStoresByKeyword(keyword, page, size);

		List<StoreSearchResponseDto> content = stores.stream()
			.map(StoreSearchResponseDto::fromEntity)
			.collect(Collectors.toList());

		return PageResponseDto.of(content, totalElements, page, size);
	}
}
