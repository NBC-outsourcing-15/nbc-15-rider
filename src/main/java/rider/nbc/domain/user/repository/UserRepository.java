package rider.nbc.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.exception.UserException;
import rider.nbc.domain.user.exception.UserExceptionCode;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    default User findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));
    }

    default void checkEmailDuplicate(String email) {
        if (findByEmail(email).isPresent()) {
            throw new UserException(UserExceptionCode.EMAIL_DUPLICATION);
        }
    }

    boolean existsByNickname(String nickname);

    default void checkNicknameDuplicate(String nickname) {
        if (existsByNickname(nickname)) {
            throw new UserException(UserExceptionCode.NICKNAME_DUPLICATION);
        }
    }
}


