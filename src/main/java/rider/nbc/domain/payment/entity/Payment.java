package rider.nbc.domain.payment.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.payment.enums.OrderNameType;
import rider.nbc.domain.payment.enums.PayType;
import rider.nbc.domain.user.entity.User;
import rider.nbc.global.config.TimeBaseEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment")
public class Payment extends TimeBaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PayType payType;
	@Column(nullable = false)
	private Long amount;
	@Column(nullable = false)
	private String orderId;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderNameType orderName;
	@Column(nullable = false)
	private String customerEmail;
	@Column(nullable = false)
	private String customerNickName;
	@Column(nullable = false)
	private String payDate;
	private String paymentKey;
	private String paySuccessYn;
	private String payFailReason;
	private boolean isCanceled;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private User user;
}
