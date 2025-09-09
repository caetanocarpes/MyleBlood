package com.sangue.api.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global para capturar exceções da API.
 * - Validações (400)
 * - Não autorizado (401)
 * - Não encontrado (404)
 * - Conflitos (409)
 * - Erros inesperados (500)
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Modelo de resposta de erro padronizado.
     */
    private Map<String, Object> buildError(HttpStatus status, String message, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        return body;
    }

    /**
     * Erros de validação Bean Validation (ex.: campos @NotBlank, @Email, etc.)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(fieldName, message);
        });

        Map<String, Object> body = buildError(HttpStatus.BAD_REQUEST,
                "Erro de validação nos campos enviados", request.getRequestURI());
        body.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Não autorizado (ex.: token inválido, sem login).
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            SecurityException ex, HttpServletRequest request
    ) {
        Map<String, Object> body = buildError(HttpStatus.UNAUTHORIZED,
                ex.getMessage() != null ? ex.getMessage() : "Não autorizado",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Entidade não encontrada (ex.: usuário, agendamento inexistente).
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            EntityNotFoundException ex, HttpServletRequest request
    ) {
        Map<String, Object> body = buildError(HttpStatus.NOT_FOUND,
                ex.getMessage() != null ? ex.getMessage() : "Recurso não encontrado",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Conflito (ex.: email/CPF já cadastrado).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(
            IllegalStateException ex, HttpServletRequest request
    ) {
        Map<String, Object> body = buildError(HttpStatus.CONFLICT,
                ex.getMessage() != null ? ex.getMessage() : "Conflito de dados",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Erros inesperados → 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(
            Exception ex, HttpServletRequest request
    ) {
        Map<String, Object> body = buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno no servidor", request.getRequestURI());
        // Em dev, pode logar o stacktrace aqui
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
