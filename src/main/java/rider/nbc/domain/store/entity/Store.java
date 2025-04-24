package rider.nbc.domain.store.entity;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.review.entity.StoreReview;
import rider.nbc.domain.store.dto.StoreUpdateRequestDto;
import rider.nbc.domain.user.entity.User;
import rider.nbc.global.config.TimeBaseEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@SQLDelete(sql = "UPDATE stores SET store_status = 'CLOSED_PERMANENTLY' WHERE store_id = ?")
@SQLRestriction("store_status != 'CLOSED_PERMANENTLY'")
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

	@Embedded
	private StoreAddress address;

	@Column
	private String storePictureUrl;

	@Column
	private String introduce;

	@Column
	private Long minDeliveryPrice;

	@Embedded
	private OperatingHours operatingHours;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private StoreStatus storeStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private User owner;

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
	private List<Menu> menus;

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
	private List<StoreReview> storeReviews;

	public void update(StoreUpdateRequestDto request) {
		this.name = request.getName();
		this.category = request.getCategory();
		this.address = new StoreAddress(request.getCity(), request.getDistrict(), request.getDetailedAddress());
		this.storePictureUrl = request.getStorePictureUrl();
		this.introduce = request.getIntroduce();
		this.operatingHours = new OperatingHours(request.getOpenTime(), request.getCloseTime());
		this.minDeliveryPrice = request.getMinDeliveryPrice();
		this.storeStatus = request.getStoreStatus();
	}

	public boolean isOwner(User user) {
		return owner.getId().equals(user.getId());
	}

	public boolean contains(Menu menu) {
		return menus.stream()
			.anyMatch(menuItem -> menuItem.equals(menu));
	}

	/**
	 * 메뉴 추가 편의 메서드
	 *
	 * @param menu 추가할 메뉴
	 */
	public void addMenu(Menu menu) {
		menus.add(menu);
		menu.setStore(this);
	}

	/**
	 * 메뉴 제거 편의 메서드
	 *
	 * @param menu 제거할 메뉴
	 */
	public void removeMenu(Menu menu) {
		menus.remove(menu);
		menu.setStore(null);
	}

}
