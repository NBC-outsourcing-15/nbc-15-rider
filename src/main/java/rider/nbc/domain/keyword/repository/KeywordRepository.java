package rider.nbc.domain.keyword.repository;

import java.util.List;
import java.util.Set;

import rider.nbc.domain.store.entity.Store;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 25.
 */
public interface KeywordRepository {
	void insertKeywordsAndMappings(Long storeId, Set<String> words);

	List<Store> searchStoresByKeyword(String keyword, int page, int size);

	Long countStoresByKeyword(String keyword);
}
