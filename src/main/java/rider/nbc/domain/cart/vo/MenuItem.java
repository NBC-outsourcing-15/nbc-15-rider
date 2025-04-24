package rider.nbc.domain.cart.vo;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
	private Long menuId;
	private Long price;
	private String name;
    private int quantity;

	public void updateQuantity(int quantity){
		this.quantity = quantity;
	}

	public void setInfos(Long price, String name){
		this.price = price;
		this.name = name;
	}
}
