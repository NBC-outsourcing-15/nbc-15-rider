package rider.nbc.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rider.nbc.domain.store.exception.StoreException;
import rider.nbc.domain.store.exception.StoreExceptionCode;
import rider.nbc.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	default User findByOwnerIdOrThrow(Long id) {
		return findById(id).orElseThrow(() -> new StoreException(StoreExceptionCode.OWNER_NOT_FOUND));
	}
}
