package rider.nbc.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import rider.nbc.domain.user.exception.UserException;
import rider.nbc.domain.user.exception.UserExceptionCode;
import rider.nbc.domain.user.repository.UserRepository;
import rider.nbc.global.config.TimeBaseEntity;

/**
 * @author : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "users")
public class User extends TimeBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private String nickname;

    @Column
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column
    private Long point;

    @Column(unique = true)
    private String socialId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType = SocialType.NORMAL;

    public void validatePassword(String rawPassword, PasswordEncoder encoder) {
        if (!encoder.matches(rawPassword, this.password)) {
            throw new UserException(UserExceptionCode.INVALID_PASSWORD);
        }
    }

    public void checkEmailDuplicate(UserRepository userRepository, String newEmail) {
        if (!this.email.equals(newEmail) && userRepository.findByEmail(newEmail).isPresent()) {
            throw new UserException(UserExceptionCode.EMAIL_DUPLICATION);
        }
    }

    public void updateUserInfo(String email, String nickname, String phone) {
        this.email = email;
        this.nickname = nickname;
        this.phone = phone;
    }

    public void changePassword(String newPassword, PasswordEncoder encoder) {
        this.password = encoder.encode(newPassword);
    }

    public void softDelete() {
        this.status = UserStatus.DELETE;
    }


    public void validateIsActive() {
        if (this.status != UserStatus.ACTIVE) {
            throw new UserException(UserExceptionCode.USER_DELETED);
        }
    }

    // Store CEO 확인용 로직
    public boolean isCEO() {
        return role.equals(Role.ROLE_CEO);
    }

    // 결제 성공으로 인한 포인트 추가
    public void plusPoint(Long amount) {
        this.point += amount;
    }

    // 결제 취소로 인한 포인트 차감
    public void minusPoint(Long amount) {
        this.point -= amount;
    }
}
