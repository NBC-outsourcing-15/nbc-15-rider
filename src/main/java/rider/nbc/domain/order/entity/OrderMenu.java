package rider.nbc.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.cart.vo.MenuItem;

@Entity
@Getter
@NoArgsConstructor
@Table(name="ordermenus")
public class OrderMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private String menuName;

    @Column(nullable = false)
    private int quantity;

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderMenu(Order order, MenuItem menuItem){
        this.order = order;
        this.menuName = menuItem.getName();
        this.price = menuItem.getPrice();
        this.quantity = menuItem.getQuantity();
    }
}
