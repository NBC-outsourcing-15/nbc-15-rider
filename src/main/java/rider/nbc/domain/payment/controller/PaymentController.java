package rider.nbc.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rider.nbc.domain.payment.dto.request.PaymentRequest;
import rider.nbc.domain.payment.dto.response.PaymentFailResponse;
import rider.nbc.domain.payment.dto.response.PaymentResponse;
import rider.nbc.domain.payment.service.PaymentService;
import rider.nbc.global.response.CommonResponse;

@RestController
@RequestMapping("/api/v1/points/charge")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<CommonResponse<PaymentResponse>> requestPayments(
            Authentication authentication, // 추후 어노테이션으로 값을 받도록 변경
            @RequestBody @Valid PaymentRequest paymentRequest
    ) {
        Object principal = authentication.getPrincipal();
        Long userId= Long.valueOf(principal.toString());
        PaymentResponse response = paymentService.requestPayment(paymentRequest,userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(true, HttpStatus.CREATED.value(), "결제 요청 성공", response));
    }

    @GetMapping("/success")
    public ResponseEntity<CommonResponse<String>> requestFinalPayments(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount
    ) {
        paymentService.requestSuccess(paymentKey, orderId, amount);
        return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "결제 완료, 금액 : " + amount));
    }

    @GetMapping("/fail")
    public ResponseEntity<CommonResponse<PaymentFailResponse>> requestFail(
            @RequestParam(name = "code") String errorCode,
            @RequestParam(name = "message") String errorMsg,
            @RequestParam(name = "orderId") String orderId
    ) {
        PaymentFailResponse response = paymentService.requestFail(errorCode, errorMsg, orderId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.of(false, HttpStatus.BAD_REQUEST.value(), response.getErrorMsg(), response));
    }

    @PostMapping("/cancel")
    public ResponseEntity<CommonResponse<String>> requestPaymentCancel(
            @RequestParam String paymentKey,
            @RequestParam String cancelReason
    ) {
        paymentService.paymentCancel(paymentKey, cancelReason);
        return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "결제 취소 완료, 이유 : " + cancelReason));
    }
}
