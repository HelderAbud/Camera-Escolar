package com.faceblogai.api;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("request.invalid_argument {}", ex.getMessage());
        ProblemDetail pd = baseProblem(
                HttpStatus.BAD_REQUEST,
                "Parâmetro inválido",
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("request.validation_failed");
        ProblemDetail pd = baseProblem(
                HttpStatus.BAD_REQUEST,
                "Erro de validação",
                "Um ou mais campos estão inválidos.",
                request.getRequestURI());

        List<Map<String, Object>> errors =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(this::fieldErrorToMap)
                        .toList();
        pd.setProperty("errors", errors);

        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        log.warn("request.access_denied {}", ex.getMessage());
        ProblemDetail pd =
                baseProblem(
                        HttpStatus.FORBIDDEN,
                        "Acesso negado",
                        "Você não tem permissão para acessar este recurso.",
                        request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {
        log.warn("request.unauthenticated {}", ex.getMessage());
        ProblemDetail pd =
                baseProblem(
                        HttpStatus.UNAUTHORIZED,
                        "Não autenticado",
                        "É necessário autenticação para acessar este recurso.",
                        request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("request.type_mismatch {}", ex.getMessage());
        ProblemDetail pd =
                baseProblem(
                        HttpStatus.BAD_REQUEST,
                        "Parâmetro inválido",
                        "Um ou mais parâmetros estão inválidos.",
                        request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("request.message_not_readable {}", ex.getMessage());
        ProblemDetail pd =
                baseProblem(
                        HttpStatus.BAD_REQUEST,
                        "Parâmetro inválido",
                        "Um ou mais parâmetros estão inválidos.",
                        request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ProblemDetail> handleErrorResponseException(
            ErrorResponseException ex, HttpServletRequest request) {
        ProblemDetail pd = ex.getBody();
        if (pd.getInstance() == null) {
            pd.setInstance(URI.create(request.getRequestURI()));
        }
        Object ts = pd.getProperties() == null ? null : pd.getProperties().get("timestamp");
        if (ts == null) {
            pd.setProperty("timestamp", Instant.now().toString());
        }
        return ResponseEntity.status(ex.getStatusCode()).body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("request.unhandled_exception", ex);
        ProblemDetail pd = baseProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                "Ocorreu um erro inesperado.",
                request.getRequestURI());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    private ProblemDetail baseProblem(
            HttpStatus status,
            String title,
            String detail,
            String instancePath) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create("about:blank"));
        pd.setInstance(URI.create(instancePath));
        pd.setProperty("timestamp", Instant.now().toString());
        return pd;
    }

    private Map<String, Object> fieldErrorToMap(FieldError fe) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("field", fe.getField());
        m.put("message", fe.getDefaultMessage());
        Object rejected = fe.getRejectedValue();
        if (rejected != null) {
            m.put("rejectedValue", rejected);
        }
        return m;
    }
}

