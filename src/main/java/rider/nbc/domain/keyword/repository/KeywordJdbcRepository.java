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
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.entity.StoreStatus;

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

	private static final RowMapper<Store> storeRowMapper = (rs, rowNum) -> Store.builder()
		.id(rs.getLong("store_id"))
		.name(rs.getString("name"))
		.storePictureUrl(rs.getString("store_picture_url"))
		.storeStatus(StoreStatus.valueOf(rs.getString("store_status")))
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

	@Override
	public List<Store> searchStoresByKeyword(String keyword, int page, int size) {
		String sql = """
			SELECT DISTINCT s.store_id, s.name, s.store_picture_url, s.store_status
			FROM stores s
			JOIN store_keyword sk ON s.store_id = sk.store_id
			JOIN keyword k ON sk.keyword_id = k.id
			WHERE k.word LIKE :keyword
			AND s.store_status != 'CLOSED_PERMANENTLY'
			""";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("keyword", "%" + keyword + "%");
		params.addValue("limit", size);
		params.addValue("offset", page * size);

		return jdbcTemplate.query(sql, params, storeRowMapper);
	}

	@Override
	public Long countStoresByKeyword(String keyword) {
		String sql = """
			SELECT COUNT(DISTINCT s.store_id)
			FROM stores s
			JOIN store_keyword sk ON s.store_id = sk.store_id
			JOIN keyword k ON sk.keyword_id = k.id
			WHERE k.word LIKE :keyword
			AND s.store_status != 'CLOSED_PERMANENTLY'
			""";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("keyword", "keyword" + "%");

		return jdbcTemplate.queryForObject(sql, params, Long.class);
	}
}
