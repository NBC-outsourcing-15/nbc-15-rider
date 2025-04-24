package rider.nbc.domain.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.cart.entity.Cart;
import rider.nbc.domain.order.enums.OrderStatus;
import rider.nbc.domain.store.entity.Store;
import rider.nbc.domain.user.entity.User;
import rider.nbc.global.config.TimeBaseEntity;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order extends TimeBaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Long totalPrice;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus = OrderStatus.WAITING;

	@ManyToOne
	@JoinColumn(name = "store_id")
	private Store store;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public void updateStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
}
