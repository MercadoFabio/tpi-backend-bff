package io.github.mercadofabio.tpibackendbff.exception;

import io.github.mercadofabio.tpibackendbff.dto.ApiErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleUserNotFound(UserNotFoundException exception, HttpServletRequest request) {
        ApiErrorDto body = new ApiErrorDto(HttpStatus.NOT_FOUND.value(), exception.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UpstreamServiceException.class)
    public ResponseEntity<ApiErrorDto> handleUpstreamError(UpstreamServiceException exception, HttpServletRequest request) {
        ApiErrorDto body = new ApiErrorDto(HttpStatus.BAD_GATEWAY.value(), exception.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }
}
