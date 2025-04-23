package rider.nbc.domain.cart.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.cart.vo.MenuItem;
import rider.nbc.global.config.TimeBaseEntity;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Getter
@Entity
@Table(name = "cart")
@NoArgsConstructor
public class Cart extends TimeBaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Long userid;

	@Column
	private Long storeId;

	// TODO 고쳐야 됨
	@ElementCollection
	private List<MenuItem> menus = new ArrayList<>();

	public Cart(Long userid, Long storeId, MenuItem menuItem){
		this.userid = userid;
		this.storeId = storeId;
		this.menus.add(menuItem);
	}
}
