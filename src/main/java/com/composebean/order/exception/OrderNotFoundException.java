package com.composebean.order.exception;

import com.composebean.global.exception.BusinessException;
import com.composebean.global.exception.ErrorCode;

public class OrderNotFoundException extends BusinessException {

    public OrderNotFoundException() {
        super(ErrorCode.ORDER_NOT_FOUND);
    }
}