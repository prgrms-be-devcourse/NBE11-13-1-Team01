package com.composebean.order.controller;

import com.composebean.global.exception.GlobalExceptionHandler;
import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.PaymentStatus;
import com.composebean.order.dto.OrderCreateResponse;
import com.composebean.order.dto.OrderDetailResponse;
import com.composebean.order.dto.OrderItemResponse;
import com.composebean.order.service.OrderCreateService;
import com.composebean.order.service.OrderDetailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderCreateService orderCreateService;

    @MockitoBean
    private OrderDetailService orderDetailService;

    @Test
    @DisplayName("주문을 생성하면 201과 생성된 주문을 반환한다")
    void createOrder() throws Exception {
        OrderCreateResponse response = OrderCreateResponse.builder()
                .orderId(1L)
                .email("customer@example.com")
                .totalPrice(16000L)
                .paymentStatus(PaymentStatus.PAID)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .deliveryDate(LocalDate.of(2026, 7, 24))
                .orderedAt(LocalDateTime.of(2026, 7, 21, 13, 30))
                .build();

        when(orderCreateService.createOrder(any()))
                .thenReturn(response);

        String request = """
                {
                  "email": "customer@example.com",
                  "address": "서울특별시 강남구 테헤란로 123",
                  "postalCode": "06234",
                  "items": [
                    {
                      "productId": 1,
                      "quantity": 2
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.totalPrice").value(16000))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"))
                .andExpect(jsonPath("$.deliveryStatus")
                        .value("PREPARING"));

        verify(orderCreateService).createOrder(any());
    }

    @Test
    @DisplayName("주문 ID로 주문 상세 정보를 조회한다")
    void getOrder() throws Exception {
        OrderItemResponse item = OrderItemResponse.builder()
                .id(1L)
                .productId(1L)
                .productName("Colombia Nariño")
                .quantity(2)
                .unitPrice(5000L)
                .subtotal(10000L)
                .build();
        OrderDetailResponse response = OrderDetailResponse.builder()
                .id(15L)
                .email("customer@example.com")
                .address("서울특별시 강남구 테헤란로 123")
                .postalCode("06234")
                .totalPrice(10000L)
                .paymentStatus(PaymentStatus.PAID)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .deliveryExpectedDate(LocalDate.of(2026, 7, 24))
                .orderedAt(LocalDateTime.of(2026, 7, 21, 13, 30))
                .items(List.of(item))
                .build();

        when(orderDetailService.getOrder(15L)).thenReturn(response);

        mockMvc.perform(get("/api/orders/{orderId}", 15L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.email")
                        .value("customer@example.com"))
                .andExpect(jsonPath("$.items[0].productName")
                        .value("Colombia Nariño"))
                .andExpect(jsonPath("$.items[0].subtotal")
                        .value(10000));

        verify(orderDetailService).getOrder(15L);
    }

    @Test
    @DisplayName("주문 생성 요청값이 올바르지 않으면 400을 반환한다")
    void createOrderValidationFail() throws Exception {
        String request = """
                {
                  "email": "wrong-email",
                  "address": "",
                  "postalCode": "",
                  "items": []
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

}
