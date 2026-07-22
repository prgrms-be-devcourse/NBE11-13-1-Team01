package com.composebean.global.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();

        ErrorResponse response = ErrorResponse.of(
                errorCode,
                exception.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError
                : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_REQUEST,
                errors
        );

        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(response);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        exception.getParameterValidationResults().forEach(result -> {
            String field = result.getMethodParameter().getParameterName();

            result.getResolvableErrors().forEach(error ->
                    errors.putIfAbsent(
                            field != null ? field : "parameter",
                            error.getDefaultMessage()
                    )
            );
        });

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_REQUEST,
                errors
        );

        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        exception.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            String field = path.substring(path.lastIndexOf('.') + 1);

            errors.putIfAbsent(field, violation.getMessage());
        });

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_REQUEST,
                errors
        );

        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception
    ) {
        ErrorResponse response = ErrorResponse.from(
                ErrorCode.INVALID_REQUEST
        );

        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception exception
    ) {
        log.error("예상하지 못한 예외가 발생했습니다.", exception);

        ErrorResponse response = ErrorResponse.from(
                ErrorCode.INTERNAL_SERVER_ERROR
        );

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(response);
    }
}