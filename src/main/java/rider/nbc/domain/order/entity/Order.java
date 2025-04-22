package rider.nbc.domain.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.cart.entity.Cart;
import rider.nbc.domain.order.enums.OrderStatus;
import rider.nbc.global.config.TimeBaseEntity;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order extends TimeBaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private int totalPrice = 0;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@OneToOne
	@JoinColumn(name = "cart_id")
	private Cart cart;

	public Order(Cart cart) {

		this.orderStatus = OrderStatus.WAITING;
	}

	public void updateStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
}
