package rider.nbc.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rider.nbc.domain.payment.dto.request.PaymentCancelRequest;
import rider.nbc.domain.payment.dto.request.PaymentRequest;
import rider.nbc.domain.payment.dto.response.PaymentFailResponse;
import rider.nbc.domain.payment.dto.response.PaymentResponse;
import rider.nbc.domain.payment.service.PaymentService;
import rider.nbc.global.auth.AuthUser;
import rider.nbc.global.response.CommonResponse;

@RestController
@RequestMapping("/api/v1/points/charge")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<CommonResponse<PaymentResponse>> requestPayment(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid PaymentRequest paymentRequest
    ) {
        PaymentResponse response = paymentService.requestPayment(paymentRequest, authUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(true, HttpStatus.CREATED.value(), "결제 요청 성공", response));
    }

    @GetMapping("/success")
    public ResponseEntity<CommonResponse<String>> requestSuccess(
            @RequestParam(name = "paymentKey") String paymentKey,
            @RequestParam(name = "orderId") String orderId,
            @RequestParam(name = "amount") Long amount
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
    public ResponseEntity<CommonResponse<String>> paymentCancel(@RequestBody PaymentCancelRequest paymentCancelRequest) {
        paymentService.paymentCancel(paymentCancelRequest);
        return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value()
                , "결제 취소 완료, 이유 : " + paymentCancelRequest.getCancelReason()));
    }
}
