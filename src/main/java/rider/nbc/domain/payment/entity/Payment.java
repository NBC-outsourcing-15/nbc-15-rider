package rider.nbc.domain.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rider.nbc.domain.payment.enums.OrderNameType;
import rider.nbc.domain.payment.enums.PayType;
import rider.nbc.domain.user.entity.User;
import rider.nbc.global.config.TimeBaseEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "payment")
public class Payment extends TimeBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PayType payType;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderNameType orderName;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String customerNickName;

    @Column(nullable = false)
    private String payDate;

    private String paymentKey;

    private String paySuccessYn;

    private String payFailReason;

    private boolean isCanceled;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private User user;


    public void updatePayFailReason(String reason) {
        this.payFailReason = reason;
    }

    public void updatePaySuccessYn(String value) {
        this.paySuccessYn = value;
    }

    public void updatePaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    public void updateCanceled() {
        this.isCanceled = true;
    }
}
