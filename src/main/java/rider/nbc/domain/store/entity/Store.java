package rider.nbc.domain.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.user.entity.User;
import rider.nbc.global.config.TimeBaseEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stores")
public class Store extends TimeBaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "store_id")
	private Long id;

	@Column
	private String name;

	@Column
	private String category;

	// TODO 값 타입으로 넣을거임
	@Column
	private String address;

	@Column(nullable = true)
	private String storePictureUrl;

	@Column
	private String content;

	@Column
	private Long minDeliveryPrice;

	// TODO 운영시간 변경 예정
	@Column
	private String operatingHours;

	// TODO 휴무일 변경 예정
	@Column
	private String holidays;

	@Column
	@Enumerated(value = EnumType.STRING)
	private StoreStatus storeStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private User owner;
}
