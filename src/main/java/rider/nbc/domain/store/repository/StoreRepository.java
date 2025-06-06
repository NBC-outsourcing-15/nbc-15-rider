package rider.nbc.domain.store.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.store.exception.StoreException;
import rider.nbc.domain.store.exception.StoreExceptionCode;
import rider.nbc.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * @author : kimjungmin
 * Created on : 2025. 4. 22.
 */
public interface StoreRepository extends JpaRepository<Store, Long> {
    /**
     * 특정 사용자가 소유한 가게의 수를 조회
     *
     * @param owner 가게 소유자
     * @return 소유자의 가게 수
     */
    long countByOwner(User owner);

    @EntityGraph(attributePaths = {"menus"})
    Optional<Store> findStoreWithMenusById(Long id);

    @Query("select s from Store s " +
            "left join fetch s.storeReviews sr " +
            "left join fetch  sr.menuReviews " +
            "where s.id = :id " +
            "order by sr.createdAt")
    Optional<Store> findStoreWithStoreReviewsById(@Param("id") Long id);

    default Store findStoreWithMenusByIdOrElseThrow(Long storeId) {
        return findStoreWithMenusById(storeId)
                .orElseThrow(() -> new StoreException(StoreExceptionCode.NOT_FOUND_STORE));
    }

    default Store findByIdOrElseThrow(Long storeId) {
        return findById(storeId).orElseThrow(() -> new StoreException(StoreExceptionCode.NOT_FOUND_STORE));
    }

    /**
     * 카테고리별로 가게 목록을 조회 (카테고리가 null이면 모든 가게 조회)
     *
     * @param category 카테고리 (optional)
     * @param pageable 페이징 정보
     * @return 가게 목록
     */
    Page<Store> findByCategoryIgnoreCase(String category, Pageable pageable);

    /**
     * 모든 가게 목록을 조회
     *
     * @param pageable 페이징 정보
     * @return 가게 목록
     */
    Page<Store> findAll(Pageable pageable);
}
