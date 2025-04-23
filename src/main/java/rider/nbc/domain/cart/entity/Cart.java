package rider.nbc.domain.cart.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import rider.nbc.domain.cart.vo.MenuItem;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Getter
@NoArgsConstructor
@RedisHash("cart")
public class Cart implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	private Long userid;

	private Long storeId;

	// TODO 고쳐야 됨
	private List<MenuItem> menus = new ArrayList<>();

	public Cart(Long userid, Long storeId, MenuItem menuItem){
		this.userid = userid;
		this.storeId = storeId;
		this.menus.add(menuItem);
	}
}
