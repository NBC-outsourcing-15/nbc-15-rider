package rider.nbc.domain.keyword.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import rider.nbc.domain.keyword.repository.KeywordRepository;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.global.util.KoreanTokenizerUtil;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 25.
 */
@Service
@RequiredArgsConstructor
public class KeywordService {

	public static final Integer MIN_KEYWORD_LENGTH = 2;
	public static final Integer MAX_KEYWORD_LENGTH = 5;

	private final KeywordRepository keywordRepository;

	@Transactional
	public void insertKeyword(Store store) {
		// 메뉴, 가게 이름, 가게 소개 기반으로 키워드 추출
		Set<String> keywords = new HashSet<>();
		keywords.addAll(KoreanTokenizerUtil.extractNouns(store.getName()));
		keywords.addAll(KoreanTokenizerUtil.extractNouns(store.getIntroduce()));

		List<Menu> menus = store.getMenus();
		if (menus != null) {
			menus.forEach(menu -> keywords.addAll(KoreanTokenizerUtil.extractNouns(menu.getName())));
		}

		// 2 ~ 5자리 키워드로 추출
		Set<String> filteredKeywords = keywords.stream()
			.filter(keyword -> keyword.length() >= MIN_KEYWORD_LENGTH && keyword.length() <= MAX_KEYWORD_LENGTH)
			.collect(Collectors.toSet());

		keywordRepository.insertKeywordsAndMappings(store.getId(), filteredKeywords);
	}

	@Transactional
	public void insertKeyword(Long storeId, Menu menu) {
		Set<String> keywords = new HashSet<>(KoreanTokenizerUtil.extractNouns(menu.getName()));

		Set<String> filteredKeywords = keywords.stream()
			.filter(keyword -> keyword.length() >= MIN_KEYWORD_LENGTH && keyword.length() <= MAX_KEYWORD_LENGTH)
			.collect(Collectors.toSet());

		keywordRepository.insertKeywordsAndMappings(storeId, filteredKeywords);
	}
}
