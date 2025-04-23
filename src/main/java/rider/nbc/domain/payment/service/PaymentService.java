package rider.nbc.domain.payment.service;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rider.nbc.domain.payment.constant.PaymentConstants;
import rider.nbc.domain.payment.dto.request.PaymentCancelRequest;
import rider.nbc.domain.payment.dto.request.PaymentRequest;
import rider.nbc.domain.payment.dto.response.PaymentFailResponse;
import rider.nbc.domain.payment.dto.response.PaymentResponse;
import rider.nbc.domain.payment.entity.Payment;
import rider.nbc.domain.payment.exception.PaymentException;
import rider.nbc.domain.payment.exception.code.PaymentExceptionCode;
import rider.nbc.domain.payment.repository.PaymentRepository;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.repository.UserRepository;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${payment.toss.test_client_secret_api_key}")
    private String testSecretApiKey;
    @Value("${payment.toss.success_url}")
    private String successCallBackUrl;
    @Value("${payment.toss.fail_url}")
    private String failCallBackUrl;
    @Value("${payment.toss.origin_url}")
    private String tossOriginUrl;

    /**
     * 결제 요청
     *
     * @param paymentRequest 요청 정보
     * @param userId         유저 id
     * @return 결제 요청 정보
     * @author 이승현
     */
    @Transactional
    public PaymentResponse requestPayment(PaymentRequest paymentRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

        Payment payment = Payment.builder()
                .orderId(UUID.randomUUID().toString())
                .payType(paymentRequest.getPayType())
                .amount(paymentRequest.getAmount())
                .orderName(paymentRequest.getOrderName())
                .payDate(String.valueOf(LocalDate.now()))
                .user(user)
                .customerEmail(user.getEmail())
                .customerNickName(user.getNickname())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        PaymentResponse paymentResponse = PaymentResponse.from(savedPayment);
        paymentResponse.updateSuccessAndFailUrl(successCallBackUrl, failCallBackUrl);

        return paymentResponse;
    }

    /**
     * 결제 성공
     *
     * @param paymentKey toss에서 redirect 시 param으로 주는 값
     * @param orderId    결제 요청 시 생성한 주문 id
     * @param amount     결제 금액
     * @return 결제 요청 정보
     * @author 이승현
     */
    @Transactional
    public PaymentResponse requestSuccess(String paymentKey, String orderId, Long amount) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentException(PaymentExceptionCode.PAYMENT_REQUEST_NOT_FOUND));

        if (!payment.getAmount().equals(amount)) {
            throw new PaymentException(PaymentExceptionCode.PAYMENT_ERROR_ORDER_AMOUNT);
        }

        payment.updatePaymentKey(paymentKey);

        HttpHeaders httpHeaders = new HttpHeaders();

        testSecretApiKey = testSecretApiKey + ":";
        String encodedAuth = new String(Base64.getEncoder().encode(testSecretApiKey.getBytes(StandardCharsets.UTF_8)));

        httpHeaders.setBasicAuth(encodedAuth);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        JSONObject param = new JSONObject();
        param.put(PaymentConstants.ORDER_ID, orderId);
        param.put(PaymentConstants.AMOUNT, amount);

        try {
            restTemplate.postForEntity(tossOriginUrl + paymentKey,
                    new HttpEntity<>(param, httpHeaders),
                    String.class);
        } catch (HttpClientErrorException e) {
            throw new PaymentException(PaymentExceptionCode.ALREADY_PROCESSED_PAYMENT);
        }

        payment.updatePaySuccessYn(PaymentConstants.SUCCESS_Y);
        payment.getUser().plusPoint(amount);

        return PaymentResponse.from(payment);
    }

    /**
     * 결제 실패
     *
     * @param errorCode toss에서 redirect 시 param으로 주는 값
     * @param errorMsg  toss에서 redirect 시 param으로 주는 값
     * @param orderId   결제 요청 시 생성한 주문 id
     * @return 결제 실패 응답
     * @author 이승현
     */
    @Transactional
    public PaymentFailResponse requestFail(String errorCode, String errorMsg, String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentException(PaymentExceptionCode.PAYMENT_REQUEST_NOT_FOUND));
        payment.updatePaySuccessYn(PaymentConstants.SUCCESS_N);
        payment.updatePayFailReason(errorMsg);

        return PaymentFailResponse.builder()
                .orderId(orderId)
                .errorCode(errorCode)
                .errorMsg(errorMsg)
                .build();
    }

    /**
     * 결제 완료된 건에 대해서 취소
     *
     * @param paymentCancelRequest 결제 요청에서 toss에서 받은 key, 취소 이유
     * @return 결제 요청 정보
     * * @author 이승현
     */
    @Transactional
    public PaymentResponse paymentCancel(PaymentCancelRequest paymentCancelRequest) {
        Payment payment = paymentRepository.findByPaymentKey(paymentCancelRequest.getPaymentKey())
                .orElseThrow(() -> new PaymentException(PaymentExceptionCode.PAYMENT_REQUEST_NOT_FOUND));

        if (payment.getPaySuccessYn() == null || !payment.getPaySuccessYn().equals(PaymentConstants.SUCCESS_Y)) {
            throw new PaymentException(PaymentExceptionCode.PAYMENT_NOT_SUCCESS);
        }

        if (payment.isCanceled()) {
            throw new PaymentException(PaymentExceptionCode.PAYMENT_ALREADY_CANCELED);
        }

        if (payment.getUser().getPoint() < payment.getAmount()) {
            throw new PaymentException(PaymentExceptionCode.PAYMENT_CANCEL_ERROR);
        }

        URI uri = URI.create(tossOriginUrl + paymentCancelRequest.getPaymentKey() + "/cancel");

        HttpHeaders httpHeaders = new HttpHeaders();
        byte[] secretKeyByte = (testSecretApiKey + ":").getBytes(StandardCharsets.UTF_8);
        httpHeaders.setBasicAuth(new String(Base64.getEncoder().encode(secretKeyByte)));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        JSONObject param = new JSONObject();
        param.put(PaymentConstants.CANCEL_REASON, paymentCancelRequest.getCancelReason());

        try {
            restTemplate.postForObject(uri,
                    new HttpEntity<>(param, httpHeaders), String.class);
        } catch (HttpClientErrorException e) {
            throw new PaymentException(PaymentExceptionCode.PAYMENT_CANCEL_ERROR);
        }

        Long amount = payment.getAmount();
        payment.getUser().minusPoint(amount);
        payment.updateCanceled();

        return PaymentResponse.from(payment);
    }

}
