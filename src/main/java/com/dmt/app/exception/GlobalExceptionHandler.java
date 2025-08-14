package com.dmt.app.exception;

import com.dmt.app.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리 핸들러
 * 모든 API에서 발생하는 예외를 일관된 형식으로 처리합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            String.valueOf(e.getErrorCode().getCode()),
            e.getMessage()
        );
        
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    /**
     * 입력값 검증 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
        log.warn("Validation error: {}", errors);
        
        ApiResponse<List<String>> response = ApiResponse.error(
            String.valueOf(ErrorCode.INVALID_INPUT_VALUE.getCode()),
            "입력값 검증에 실패했습니다.",
            String.join(", ", errors)
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 바인딩 예외 처리
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleBindException(BindException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
        log.warn("Bind error: {}", errors);
        
        ApiResponse<List<String>> response = ApiResponse.error(
            String.valueOf(ErrorCode.INVALID_INPUT_VALUE.getCode()),
            "입력값 바인딩에 실패했습니다.",
            String.join(", ", errors)
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 제약 조건 위반 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        List<String> errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        
        log.warn("Constraint violation: {}", errors);
        
        ApiResponse<List<String>> response = ApiResponse.error(
            String.valueOf(ErrorCode.INVALID_INPUT_VALUE.getCode()),
            "제약 조건 위반이 발생했습니다.",
            String.join(", ", errors)
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * HTTP 메시지 읽기 실패 예외 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("HTTP message not readable: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            String.valueOf(ErrorCode.INVALID_INPUT_VALUE.getCode()),
            "요청 본문을 읽을 수 없습니다."
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * HTTP 메서드 지원하지 않음 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("HTTP method not supported: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            String.valueOf(ErrorCode.METHOD_NOT_ALLOWED.getCode()),
            "지원하지 않는 HTTP 메서드입니다: " + e.getMethod()
        );
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * 핸들러를 찾을 수 없음 예외 처리
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("No handler found: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            String.valueOf(ErrorCode.ENTITY_NOT_FOUND.getCode()),
            "요청한 리소스를 찾을 수 없습니다: " + e.getRequestURL()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 인증 예외 처리
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication error: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            String.valueOf(ErrorCode.UNAUTHORIZED.getCode()),
            "인증에 실패했습니다."
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 접근 거부 예외 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            String.valueOf(ErrorCode.HANDLE_ACCESS_DENIED.getCode()),
            "접근이 거부되었습니다."
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("Method argument type mismatch: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            String.valueOf(ErrorCode.INVALID_TYPE_VALUE.getCode()),
            "잘못된 타입의 값입니다: " + e.getName()
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 필수 파라미터 누락 예외 처리
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("Missing request parameter: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            String.valueOf(ErrorCode.INVALID_INPUT_VALUE.getCode()),
            "필수 파라미터가 누락되었습니다: " + e.getParameterName()
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected error occurred", e);
        
        ApiResponse<Void> response = ApiResponse.error(
            String.valueOf(ErrorCode.INTERNAL_SERVER_ERROR.getCode()),
            "서버 내부 오류가 발생했습니다."
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 