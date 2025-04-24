package rider.nbc.domain.order.vo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.cart.vo.MenuItem;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
public class OrderMenu {

    private Long price;


    private String menuName;


    private int quantity;

    public static OrderMenu from(MenuItem menuItem){
        return OrderMenu.builder()
                .menuName(menuItem.getName())
                .price(menuItem.getPrice())
                .quantity(menuItem.getQuantity())
                .build();
    }

}
