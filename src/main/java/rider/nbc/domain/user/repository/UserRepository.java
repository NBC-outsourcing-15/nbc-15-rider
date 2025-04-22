package rider.nbc.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rider.nbc.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
