package rider.nbc.domain.user.entity;

import static rider.nbc.domain.store.constant.StoreConstants.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.global.config.TimeBaseEntity;

/**
 * @author    : kimjungmin
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

	@Column
	private String role;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private UserStatus status = UserStatus.ACTIVE;

	@Column
	private long point;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private SocialType socialType = SocialType.NORMAL;

	public void validatePassword(String rawPassword, PasswordEncoder encoder) {
		if (!encoder.matches(rawPassword, this.password)) {
			throw new RuntimeException("비밀번호가 일치하지 않습니다.");
		}
	}

	public void softDelete() {
		this.status = UserStatus.DELETE;
	}

	public void validateIsActive() {
		if (this.status != UserStatus.ACTIVE) {
			throw new RuntimeException("비활성화 된 계정입니다.");
		}
	}

	// Store CEO 확인용 로직
	public boolean isCEO() {
		return ROLE_CEO.equals(role);
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
