package rider.nbc.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import rider.nbc.domain.user.entity.SocialType;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.entity.UserStatus;
import rider.nbc.domain.user.exception.UserException;
import rider.nbc.domain.user.exception.UserExceptionCode;
import rider.nbc.domain.store.exception.StoreException;
import rider.nbc.domain.store.exception.StoreExceptionCode;
import rider.nbc.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findBySocialIdAndSocialType(String socialId, SocialType socialType);


    default User findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));
    }

    default void checkEmailDuplicate(String email) {
        if (findByEmail(email).isPresent()) {
            throw new UserException(UserExceptionCode.EMAIL_DUPLICATION);
        }
    }

    default void validateSocialJoinEmail(String email) {
        if (email != null && findByEmail(email).isPresent()) {
            throw new UserException(UserExceptionCode.EMAIL_DUPLICATION);
        }
    }


    boolean existsByNickname(String nickname);

    default void checkNicknameDuplicate(String nickname) {
        if (existsByNickname(nickname)) {
            throw new UserException(UserExceptionCode.NICKNAME_DUPLICATION);
        }
    }

    default User findActiveByIdOrThrow(Long id) {
        User user = findById(id)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserException(UserExceptionCode.USER_DELETED);
        }
        return user;
    }

	default User findByOwnerIdOrThrow(Long id) {
		return findById(id).orElseThrow(() -> new StoreException(StoreExceptionCode.OWNER_NOT_FOUND));
	}
}


