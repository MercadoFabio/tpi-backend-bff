package io.github.mercadofabio.tpibackendbff.exception;

import io.github.mercadofabio.tpibackendbff.dto.ApiErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleUserNotFound(UserNotFoundException exception, HttpServletRequest request) {
        ApiErrorDto body = new ApiErrorDto(HttpStatus.NOT_FOUND.value(), "Usuario no encontrado", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UpstreamServiceException.class)
    public ResponseEntity<ApiErrorDto> handleUpstreamError(UpstreamServiceException exception, HttpServletRequest request) {
        ApiErrorDto body = new ApiErrorDto(HttpStatus.BAD_GATEWAY.value(), "Servicio temporalmente no disponible", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleInvalidRequest(MethodArgumentNotValidException exception, HttpServletRequest request) {
        ApiErrorDto body = new ApiErrorDto(HttpStatus.BAD_REQUEST.value(), "Solicitud inválida", request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorDto> handleMissingRoute(NoResourceFoundException exception, HttpServletRequest request) {
        ApiErrorDto body = new ApiErrorDto(HttpStatus.NOT_FOUND.value(), "Recurso no encontrado", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleUnexpected(Exception exception, HttpServletRequest request) {
        ApiErrorDto body = new ApiErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno", request.getRequestURI());
        return ResponseEntity.internalServerError().body(body);
    }
}
