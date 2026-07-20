package com.composebean.product.exception;

import com.composebean.global.exception.BusinessException;
import com.composebean.global.exception.ErrorCode;

public class ProductNotFoundException extends BusinessException {

    public ProductNotFoundException() {
        super(ErrorCode.PRODUCT_NOT_FOUND);
    }
}