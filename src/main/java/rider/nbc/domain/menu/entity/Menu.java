package rider.nbc.domain.menu.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.menu.dto.MenuUpdateRequestDto;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.global.config.TimeBaseEntity;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@Entity
@SQLDelete(sql = "UPDATE menus SET deleted_at = NOW() WHERE menu_id = ?")
@SQLRestriction("deleted_at is NULL")
@Table(name = "menus")
public class Menu extends TimeBaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_id")
	private Long id;

	@Column
	private String name;

	@Column // TODO 변경 예정
	private String category;

	@Column
	private Long price;

	@Column
	private String menuPictureUrl;

	@ManyToOne
	@JoinColumn(name = "store_id")
	private Store store;

	@Column
	private LocalDateTime deletedAt; // soft delete column

	/**
	 * 메뉴 정보 업데이트
	 *
	 * @param requestDto 메뉴 업데이트 요청 DTO
	 */
	public void update(MenuUpdateRequestDto requestDto) {
		this.name = requestDto.getName();
		this.category = requestDto.getCategory();
		this.price = requestDto.getPrice();
		this.menuPictureUrl = requestDto.getMenuPictureUrl();
	}

	/**
	 * 가게 설정 메서드 (연관관계 편의 메서드용)
	 *
	 * @param store 연결할 가게
	 */
	public void setStore(Store store) {
		this.store = store;
	}
}
