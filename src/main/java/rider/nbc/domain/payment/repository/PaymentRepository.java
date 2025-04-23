package rider.nbc.domain.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rider.nbc.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Optional<Payment> findByOrderId(String orderId);
	Optional<Payment> findByPaymentKey(String paymentKey);
}
