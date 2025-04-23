package rider.nbc.domain.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import rider.nbc.domain.payment.dto.request.PaymentCancelRequest;
import rider.nbc.domain.payment.dto.request.PaymentRequest;
import rider.nbc.domain.payment.dto.response.PaymentFailResponse;
import rider.nbc.domain.payment.dto.response.PaymentResponse;
import rider.nbc.domain.payment.enums.OrderNameType;
import rider.nbc.domain.payment.enums.PayType;
import rider.nbc.domain.payment.service.PaymentService;
import rider.nbc.domain.support.security.TestSecurityConfig;
import rider.nbc.domain.support.security.WithCustomMockUser;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PaymentController.class)
@Import(TestSecurityConfig.class)
class PaymentControllerTest {
    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final PaymentRequest paymentRequest = new PaymentRequest(PayType.CARD, 1000L, OrderNameType.CHARGE);

    private final PaymentCancelRequest paymentCancelRequest = new PaymentCancelRequest("paymentKey", "단순 변심");

    private final PaymentResponse paymentResponse = PaymentResponse.builder()
            .payType(PayType.CARD.name())
            .amount(1000L)
            .orderId("orderId")
            .orderName(OrderNameType.CHARGE.name())
            .customerEmail("test@test.com")
            .customerNickName("test")
            .successUrl("success")
            .failUrl("fail")
            .payDate(String.valueOf(LocalDate.now()))
            .build();

    private final PaymentFailResponse paymentFailResponse =
            PaymentFailResponse.builder()
                    .errorCode("errorCode")
                    .errorMsg("결제실패")
                    .orderId("orderId")
                    .build();

    @Test
    @WithCustomMockUser
    @DisplayName("충전 요청 성공")
    void success_requestPayment() throws Exception {
        //given
        given(paymentService.requestPayment(any(), any()))
                .willReturn(paymentResponse);

        //when
        ResultActions perform = mockMvc.perform(post("/api/v1/points/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)));

        //then
        perform.andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.success")
                                .value(true),
                        jsonPath("$.status")
                                .value(201),
                        jsonPath("$.message")
                                .value("결제 요청 성공"),
                        jsonPath("$.result.payType")
                                .value(paymentRequest.getPayType().name()),
                        jsonPath("$.result.amount")
                                .value(paymentRequest.getAmount()),
                        jsonPath("$.result.orderId")
                                .value("orderId"),
                        jsonPath("$.result.orderName")
                                .value(paymentRequest.getOrderName().name()),
                        jsonPath("$.result.customerEmail")
                                .value("test@test.com"),
                        jsonPath("$.result.customerNickName")
                                .value("test"),
                        jsonPath("$.result.successUrl")
                                .value("success"),
                        jsonPath("$.result.failUrl")
                                .value("fail"),
                        jsonPath("$.result.payDate")
                                .value(String.valueOf(LocalDate.now()))
                );
    }

    @Test
    @DisplayName("결제 완료 성공")
    void success_requestSuccess() throws Exception {
        //given
        String amount = "1000";

        //when
        ResultActions perform = mockMvc.perform(get("/api/v1/points/charge/success")
                .param("paymentKey", "paymentKey")
                .param("orderId", "orderId")
                .param("amount", amount));

        //then
        perform.andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.success")
                                .value(true),
                        jsonPath("$.status")
                                .value(200),
                        jsonPath("$.message")
                                .value("결제 완료, 금액 : " + amount)
                );

    }

    @Test
    @DisplayName("결제 실패 성공")
    void success_requestFail() throws Exception {
        //given
        given(paymentService.requestFail(any(), any(), any()))
                .willReturn(paymentFailResponse);

        //when
        ResultActions perform = mockMvc.perform(get("/api/v1/points/charge/fail")
                .param("code", "errorCode")
                .param("message", "결제실패")
                .param("orderId", "orderId"));

        //then
        perform.andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.success")
                                .value(false),
                        jsonPath("$.status")
                                .value(400),
                        jsonPath("$.message")
                                .value("결제실패"),
                        jsonPath("$.result.errorCode")
                                .value("errorCode"),
                        jsonPath("$.result.errorMsg")
                                .value("결제실패"),
                        jsonPath("$.result.orderId")
                                .value("orderId")
                );
    }

    @Test
    @DisplayName("결제 취소 성공")
    void success_paymentCancel() throws Exception {
        //given

        //when
        ResultActions perform = mockMvc.perform(post("/api/v1/points/charge/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentCancelRequest)));

        //then
        perform.andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.success")
                                .value(true),
                        jsonPath("$.status")
                                .value(200),
                        jsonPath("$.message")
                                .value("결제 취소 완료, 이유 : " + paymentCancelRequest.getCancelReason())
                );
    }

}