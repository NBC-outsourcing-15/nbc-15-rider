package rider.nbc.domain.keyword.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import rider.nbc.domain.keyword.entity.Keyword;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 25.
 */
@Repository
@RequiredArgsConstructor
public class KeywordJdbcRepository implements KeywordRepository {
	private static final RowMapper<Keyword> keywordRowMapper = (rs, rowNum) -> Keyword.builder()
		.id(rs.getLong("id"))
		.word(rs.getString("word"))
		.build();
	
	private final NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public void insertKeywordsAndMappings(Long storeId, Set<String> words) {
		if (words.isEmpty())
			return;

		// 존재하는 단어 조회

		List<Keyword> existingKeywords = jdbcTemplate.query("SELECT id, word FROM keyword WHERE word IN (:words)",
			Map.of("words", words),
			keywordRowMapper
		);

		// 존재하는 단어를 문자열로 추출
		Set<String> existingWords = existingKeywords.stream()
			.map(Keyword::getWord)
			.collect(Collectors.toSet());

		// 새로운 키워드 필터링
		Set<String> newKeywords = words.stream()
			.filter(word -> !existingWords.contains(word))
			.collect(Collectors.toSet());

		// 새로운 키워드 배치 인서트
		if (!newKeywords.isEmpty()) {
			String batchSql = "INSERT INTO keyword (word) VALUES (:word)";

			SqlParameterSource[] batchParams = newKeywords.stream()
				.map(keyword -> new MapSqlParameterSource("word", keyword))
				.toArray(SqlParameterSource[]::new);

			jdbcTemplate.batchUpdate(batchSql, batchParams);

			// 새로운 키워드와 아이디 조회
			List<Keyword> newInsertedKeywords = jdbcTemplate.query(
				"SELECT id, word FROM keyword WHERE word IN (:words)",
				Map.of("words", newKeywords),
				keywordRowMapper
			);

			// 모든 키워드 합치기
			existingKeywords.addAll(newInsertedKeywords);
		}

		// 매핑 테이블에 데이터 벌크 적재
		String batchSql = "INSERT IGNORE INTO store_keyword (store_id, keyword_id) VALUES (:storeId, :keywordId)";
		SqlParameterSource[] batchParams = existingKeywords.stream()
			.map(keyword -> new MapSqlParameterSource()
				.addValue("storeId", storeId)
				.addValue("keywordId", keyword.getId()))
			.toArray(SqlParameterSource[]::new);

		jdbcTemplate.batchUpdate(batchSql, batchParams);
	}
}
