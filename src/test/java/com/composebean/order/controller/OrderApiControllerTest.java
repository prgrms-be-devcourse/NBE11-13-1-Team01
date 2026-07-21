package com.composebean.order.controller;

import com.composebean.global.exception.GlobalExceptionHandler;

import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.PaymentStatus;
import com.composebean.order.dto.DeliveryStatusUpdateRequest;
import com.composebean.order.dto.OrderDetailResponse;
import com.composebean.order.dto.OrderItemResponse;
import com.composebean.order.repository.OrderRepository;
import com.composebean.order.service.OrderBatchService;
import com.composebean.order.service.OrderInquiryService;
import com.composebean.order.service.OrderUpdateService;
import com.composebean.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(OrderApiController.class)
@Import(GlobalExceptionHandler.class)
class OrderApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderUpdateService orderUpdateService;

    @MockitoBean
    private OrderInquiryService orderInquiryService;

    @Test
    @DisplayName("배송 상태를 수정하면 200 OK와 수정된 결과를 반환한다")
    void updateDeliveryStatus() throws Exception {

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
                .deliveryStatus(DeliveryStatus.DELIVERED)
                .deliveryExpectedDate(LocalDate.of(2026, 7, 24))
                .orderedAt(LocalDateTime.of(2026, 7, 21, 13, 30))
                .items(List.of(item))
                .build();
        DeliveryStatusUpdateRequest request = new DeliveryStatusUpdateRequest(DeliveryStatus.DELIVERED);

        //목 응답 생성
        when(orderUpdateService.updateDeliveryStatus(
                any(DeliveryStatusUpdateRequest.class),
                eq(15L)
        )).thenReturn(response);

        //가짜 요청 생성
        String requestJson = """
            {
              "deliveryStatus": "SHIPPING"
            }
            """;

        //가짜 요청 후 응답 ok받기
        mockMvc.perform(
                patch("/api/orders/{orderId}/delivery-status", 15L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
        .andExpect(status().isOk());
//        HttpStatus
        verify(orderUpdateService).updateDeliveryStatus(any(DeliveryStatusUpdateRequest.class),eq(15L) );

    }
}