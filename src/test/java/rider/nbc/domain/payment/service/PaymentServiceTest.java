package rider.nbc.domain.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rider.nbc.domain.payment.constant.PaymentConstants;
import rider.nbc.domain.payment.dto.request.PaymentCancelRequest;
import rider.nbc.domain.payment.dto.request.PaymentRequest;
import rider.nbc.domain.payment.dto.response.PaymentFailResponse;
import rider.nbc.domain.payment.dto.response.PaymentResponse;
import rider.nbc.domain.payment.entity.Payment;
import rider.nbc.domain.payment.enums.OrderNameType;
import rider.nbc.domain.payment.enums.PayType;
import rider.nbc.domain.payment.exception.PaymentException;
import rider.nbc.domain.payment.exception.code.PaymentExceptionCode;
import rider.nbc.domain.payment.repository.PaymentRepository;
import rider.nbc.domain.user.entity.User;
import rider.nbc.domain.user.exception.UserException;
import rider.nbc.domain.user.exception.UserExceptionCode;
import rider.nbc.domain.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @Spy
    private User user;

    @Spy
    private Payment payment;

    private final PaymentRequest paymentRequest = new PaymentRequest(PayType.CARD, 1000L, OrderNameType.CHARGE);

    private final PaymentCancelRequest paymentCancelRequest = new PaymentCancelRequest("paymentKey", "단순 변심");

    public static final String SUCCESS_CALL_BACK_URL = "http://localhost:8080/api/v1/points/success";

    public static final String FAIL_CALL_BACK_URL = "http://localhost:8080/api/v1/points/fail";

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("test")
                .point(1000L)
                .build();

        payment = Payment.builder()
                .orderId(UUID.randomUUID().toString())
                .payType(paymentRequest.getPayType())
                .amount(paymentRequest.getAmount())
                .orderName(paymentRequest.getOrderName())
                .payDate(String.valueOf(LocalDate.now()))
                .user(user)
                .customerEmail(user.getEmail())
                .customerNickName(user.getNickname())
                .build();
    }

    @Nested
    @DisplayName("결제 요청 테스트")
    class RequestPaymentTest {
        @Test
        @DisplayName("결제 요청 성공")
        void success_requestPayment() {
            //given
            ReflectionTestUtils.setField(paymentService, "successCallBackUrl", SUCCESS_CALL_BACK_URL);
            ReflectionTestUtils.setField(paymentService, "failCallBackUrl", FAIL_CALL_BACK_URL);

            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.ofNullable(user));
            given(paymentRepository.save(any()))
                    .willReturn(payment);

            //when
            PaymentResponse response = paymentService.requestPayment(paymentRequest, 1L);

            //then
            assertAll(
                    () -> assertEquals(payment.getId(), response.getId()),
                    () -> assertEquals(payment.getPayType().name(), response.getPayType()),
                    () -> assertEquals(1000L, response.getAmount()),
                    () -> assertEquals(payment.getOrderId().length(), response.getOrderId().length()),
                    () -> assertEquals(payment.getOrderName().name(), response.getOrderName()),
                    () -> assertEquals("test@test.com", response.getCustomerEmail()),
                    () -> assertEquals("test", response.getCustomerNickName()),
                    () -> assertEquals(SUCCESS_CALL_BACK_URL, response.getSuccessUrl()),
                    () -> assertEquals(FAIL_CALL_BACK_URL, response.getFailUrl()),
                    () -> assertEquals(payment.getPayDate(), response.getPayDate())
            );
        }

        @Test
        @DisplayName("결제 요청 실패 - 유저를 찾을 수 없음")
        void fail_requestPayment_userNotFound() {
            //given
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            //when
            UserException exception = assertThrows(UserException.class,
                    () -> paymentService.requestPayment(paymentRequest, 1L));

            //then
            assertEquals(UserExceptionCode.USER_NOT_FOUND, exception.getErrorCode());
        }

    }

    @Nested
    @DisplayName("결제 성공 테스트")
    class RequestSuccessTest {
        @Test
        @DisplayName("결제 성공")
        void success_requestSuccess() {
            //given
            given(paymentRepository.findByOrderId(anyString()))
                    .willReturn(Optional.of(payment));

            //when
            PaymentResponse response = paymentService.requestSuccess("paymentKey", "orderId", 1000L);

            //then
            assertAll(
                    () -> assertEquals("Y", response.getPaySuccessYn()),
                    () -> assertEquals("paymentKey", response.getPaymentKey()),
                    () -> assertEquals(1000L, payment.getAmount())
            );
        }

        @Test
        @DisplayName("결제 성공 실패 - 결제 요청을 찾을 수 없음")
        void fail_requestSuccess_paymentNotFound() {
            //given
            given(paymentRepository.findByOrderId(anyString()))
                    .willReturn(Optional.empty());

            //when
            PaymentException exception = assertThrows(PaymentException.class,
                    () -> paymentService.requestSuccess("paymentKey", "orderId", 1000L));

            //then
            assertEquals(PaymentExceptionCode.PAYMENT_REQUEST_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("결제 성공 실패 - 요청 금액과 결제 금액이 다름")
        void fail_requestSuccess_paymentErrorOrderAmount() {
            //given
            given(paymentRepository.findByOrderId(anyString()))
                    .willReturn(Optional.ofNullable(payment));

            //when
            PaymentException exception = assertThrows(PaymentException.class,
                    () -> paymentService.requestSuccess("paymentKey", "orderId", 10000L));

            //then
            assertEquals(PaymentExceptionCode.PAYMENT_ERROR_ORDER_AMOUNT, exception.getErrorCode());
        }

        @Test
        @DisplayName("결제 성공 실패 - 이미 처리된 결제")
        void fail_requestSuccess_alreadyProcessedPayment() {
            //given
            given(paymentRepository.findByOrderId(anyString()))
                    .willReturn(Optional.ofNullable(payment));
            given(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any()))
                    .willThrow(HttpClientErrorException.class);

            //when
            PaymentException exception = assertThrows(PaymentException.class,
                    () -> paymentService.requestSuccess("paymentKey", "orderId", 1000L));

            //then
            assertEquals(PaymentExceptionCode.ALREADY_PROCESSED_PAYMENT, exception.getErrorCode());
        }

    }

    @Nested
    @DisplayName("결제 실패 테스트")
    class RequestFailTest {
        @Test
        @DisplayName("결제 실패 성공")
        void success_requestFail() {
            //given
            given(paymentRepository.findByOrderId(anyString()))
                    .willReturn(Optional.of(payment));

            //when
            PaymentFailResponse response = paymentService.requestFail("-1", "오류", "orderId");

            //then

            assertAll(
                    () -> assertEquals(PaymentConstants.SUCCESS_N, payment.getPaySuccessYn()),
                    () -> assertEquals("오류", payment.getPayFailReason()),
                    () -> assertEquals("-1", response.getErrorCode()),
                    () -> assertEquals("오류", response.getErrorMsg()),
                    () -> assertEquals("orderId", response.getOrderId())
            );
        }

        @Test
        @DisplayName("결제 실패 실패 - 결제 요청을 찾을 수 없음")
        void fail_requestFail_paymentRequestNotFound() {
            //given
            given(paymentRepository.findByOrderId(anyString()))
                    .willReturn(Optional.empty());

            //when
            PaymentException exception = assertThrows(PaymentException.class,
                    () -> paymentService.requestFail("-1", "오류", "orderId"));

            //then
            assertEquals(PaymentExceptionCode.PAYMENT_REQUEST_NOT_FOUND, exception.getErrorCode());
        }

    }

    @Nested
    @DisplayName("결제 취소 테스트")
    class RequestPaymentCancelTest {
        @Test
        @DisplayName("결제 취소 성공")
        void success_paymentCancel() {
            //given
            given(paymentRepository.findByPaymentKey(anyString()))
                    .willReturn(Optional.ofNullable(payment.toBuilder()
                            .paySuccessYn("Y")
                            .build()));

            //when
            PaymentResponse response = paymentService.paymentCancel(paymentCancelRequest);

            //then
            assertAll(
                    () -> assertTrue(response.isCanceled()),
                    () -> assertEquals(0L, payment.getUser().getPoint())
            );
        }

        @Test
        @DisplayName("결제 취소 실패 - 결제 요청을 찾을 수 없음")
        void fail_requestPaymentCancel_paymentRequestNotFound() {
            //given
            given(paymentRepository.findByPaymentKey(anyString()))
                    .willReturn(Optional.empty());

            //when
            PaymentException exception = assertThrows(PaymentException.class,
                    () -> paymentService.paymentCancel(paymentCancelRequest));

            //then
            assertEquals(PaymentExceptionCode.PAYMENT_REQUEST_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("결제 취소 실패 - 결제 완료 요청을 찾을 수 없음, N")
        void fail_requestPaymentCancel_paymentNotSuccess_N() {
            //given
            given(paymentRepository.findByPaymentKey(anyString()))
                    .willReturn(Optional.ofNullable(payment.toBuilder()
                            .paySuccessYn(null)
                            .build()));

            //when
            PaymentException exception = assertThrows(PaymentException.class,
                    () -> paymentService.paymentCancel(paymentCancelRequest));

            //then
            assertEquals(PaymentExceptionCode.PAYMENT_NOT_SUCCESS, exception.getErrorCode());
        }

        @Test
        @DisplayName("결제 취소 실패 - 결제 완료 요청을 찾을 수 없음, null")
        void fail_requestPaymentCancel_paymentNotSuccess_null() {
            //given
            given(paymentRepository.findByPaymentKey(anyString()))
                    .willReturn(Optional.ofNullable(payment.toBuilder()
                            .paySuccessYn("N")
                            .build()));

            //when
            PaymentException exception = assertThrows(PaymentException.class,
                    () -> paymentService.paymentCancel(paymentCancelRequest));

            //then
            assertEquals(PaymentExceptionCode.PAYMENT_NOT_SUCCESS, exception.getErrorCode());
        }

        @Test
        @DisplayName("결제 취소 실패 - 결제 완료 요청을 찾을 수 없음, null")
        void fail_requestPaymentCancel_paymentAlreadyCanceled() {
            //given
            given(paymentRepository.findByPaymentKey(anyString()))
                    .willReturn(Optional.ofNullable(payment.toBuilder()
                            .paySuccessYn("Y")
                            .isCanceled(true)
                            .build()));

            //when
            PaymentException exception = assertThrows(PaymentException.class,
                    () -> paymentService.paymentCancel(paymentCancelRequest));

            //then
            assertEquals(PaymentExceptionCode.PAYMENT_ALREADY_CANCELED, exception.getErrorCode());
        }

        @Test
        @DisplayName("결제 취소 실패 - 회원의 포인트가 부족함")
        void fail_paymentCancel_validException() {
            //given
            given(paymentRepository.findByPaymentKey(anyString()))
                    .willReturn(Optional.ofNullable(payment.toBuilder()
                            .paySuccessYn("Y")
                            .user(user.toBuilder()
                                    .point(100L)
                                    .build())
                            .build()));

            //when
            PaymentException exception = assertThrows(PaymentException.class,
                    () -> paymentService.paymentCancel(paymentCancelRequest));

            //then
            assertEquals(PaymentExceptionCode.PAYMENT_CANCEL_ERROR, exception.getErrorCode());
        }

        @Test
        @DisplayName("결제 취소 실패 - toss 통신 오류")
        void fail_paymentCancel_paymentCancelError() {
            //given
            given(paymentRepository.findByPaymentKey(anyString()))
                    .willReturn(Optional.ofNullable(payment.toBuilder()
                            .paySuccessYn("Y")
                            .build()));
            given(restTemplate.postForObject(any(), any(HttpEntity.class), any()))
                    .willThrow(HttpClientErrorException.class);

            //when
            PaymentException paymentException = assertThrows(PaymentException.class,
                    () -> paymentService.paymentCancel(paymentCancelRequest));

            //then
            assertEquals(PaymentExceptionCode.PAYMENT_CANCEL_ERROR, paymentException.getErrorCode());
        }

    }

}
