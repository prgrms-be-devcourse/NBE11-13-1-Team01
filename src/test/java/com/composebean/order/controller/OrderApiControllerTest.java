package com.composebean.order.controller;

import com.composebean.global.exception.BusinessException;
import com.composebean.global.exception.ErrorCode;
import com.composebean.global.exception.GlobalExceptionHandler;

import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.PaymentStatus;
import com.composebean.order.dto.DeliveryStatusUpdateRequest;
import com.composebean.order.dto.OrderDetailResponse;
import com.composebean.order.dto.OrderItemResponse;
import com.composebean.order.service.OrderInquiryService;
import com.composebean.order.service.OrderUpdateService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Test
    @DisplayName("유효성 검증 실패")
    void validException() throws Exception {
        // given
        String invalidRequestJson = """
        {
          "deliveryStatus": null 
        }
        """;

        mockMvc.perform(
                        patch("/api/orders/{orderId}/delivery-status", 15L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequestJson)
                )
                .andExpect(status().isBadRequest());
        verify(orderUpdateService, never()).updateDeliveryStatus(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID이면 404를 반환한다")
    void notFoundException() throws Exception {
        Long invalidOrderId = 999L;

        String requestJson = """
        {
          "deliveryStatus": "SHIPPING"
        }
        """;

        when(orderUpdateService.updateDeliveryStatus(
                any(DeliveryStatusUpdateRequest.class),
                eq(invalidOrderId)
        )).thenThrow(new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        mockMvc.perform(
                        patch("/api/orders/{orderId}/delivery-status", invalidOrderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ORDER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));

        verify(orderUpdateService).updateDeliveryStatus(
                any(DeliveryStatusUpdateRequest.class),
                eq(invalidOrderId)
        );
    }

    @Test
    @DisplayName("주문 ID가 음수이면 400을 반환한다")
    void invalidOrderId() throws Exception {
        mockMvc.perform(
                        patch("/api/orders/{orderId}/delivery-status", -1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                  "deliveryStatus": "SHIPPING"
                                }
                                """)
                )
                .andExpect(status().isBadRequest());

        verify(orderUpdateService, never())
                .updateDeliveryStatus(any(), any());
    }
}