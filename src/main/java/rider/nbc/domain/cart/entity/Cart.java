package rider.nbc.domain.cart.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.cart.vo.MenuItem;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Getter
@NoArgsConstructor
public class Cart{
	@Column
	private Long userid;

	@Column
	private Long storeId;

	// TODO 고쳐야 됨
	private List<MenuItem> menus = new ArrayList<>();

	public Cart(Long userid, Long storeId, MenuItem menuItem){
		this.userid = userid;
		this.storeId = storeId;
		this.menus.add(menuItem);
	}
}
