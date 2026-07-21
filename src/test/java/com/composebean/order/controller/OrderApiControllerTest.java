package com.composebean.order.controller;

import com.composebean.order.service.OrderUpdateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(OrderApiControllerTest.class)
class OrderApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderUpdateService orderUpdateService;

    @Test
    @DisplayName("요청 성공")
    void 요청_성공() {

    }
}