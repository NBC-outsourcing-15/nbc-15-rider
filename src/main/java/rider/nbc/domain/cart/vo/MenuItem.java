package rider.nbc.domain.cart.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
	private Long menuId;
    private int quantity;
	//private List<Integer> options;

	public void updateQuantity(int quantity){
		this.quantity = quantity;
	}
}
