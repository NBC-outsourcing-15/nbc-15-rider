package rider.nbc.domain.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.user.entity.User;

/**
 * @author    : kimjungmin
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
}
