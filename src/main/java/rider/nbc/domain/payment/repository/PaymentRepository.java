package rider.nbc.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rider.nbc.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
