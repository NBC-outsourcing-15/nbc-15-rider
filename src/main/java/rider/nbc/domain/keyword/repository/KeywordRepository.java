package rider.nbc.domain.keyword.repository;

import java.util.Set;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 25.
 */
public interface KeywordRepository {
	void insertKeywordsAndMappings(Long storeId, Set<String> words);
}
