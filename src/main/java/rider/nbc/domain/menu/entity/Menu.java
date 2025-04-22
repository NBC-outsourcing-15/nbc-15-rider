package rider.nbc.domain.menu.entity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.global.config.TimeBaseEntity;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
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

	// TODO 값 타입으로 넣을거임
	@Column
	private String address;

	@Column
	private Long price;

	@Column
	private String menuPictureUrl;

	// @Column
	// @Enumerated(value = EnumType.STRING)
	// private MenuStatus menuStatus;

	@ManyToOne
	@JoinColumn(name = "store_id")
	private Store store;
}
