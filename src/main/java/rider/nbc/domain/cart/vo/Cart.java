package rider.nbc.domain.cart.vo;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Getter
@NoArgsConstructor
@RedisHash("cart")
public class Cart implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long userId;

	private Long storeId;

	private List<MenuItem> menus = new ArrayList<>();

	@TimeToLive
	private Long ttl = 86400L;

	public Cart(Long userid, Long storeId, MenuItem menuItem){
		this.userId = userid;
		this.storeId = storeId;
		this.menus.add(menuItem);
	}

	public void updateCart(Long storeId, List<MenuItem> menuItems){
		this.storeId =storeId;
		this.menus = menuItems;
	}

	public void removeMenuItem(Long menuId) {
		this.menus.removeIf(menuItem -> menuItem.getMenuId().equals(menuId));
	}

}
