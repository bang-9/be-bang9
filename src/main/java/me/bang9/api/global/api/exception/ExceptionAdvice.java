package me.bang9.api.global.api.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.bang9.api.global.api.Bang9Response;
import me.bang9.api.global.api.code.ErrorReasonDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

import static me.bang9.api.global.api.code.status.CommonErrorStatus.VALIDATION_ERROR;
import static me.bang9.api.global.api.code.status.UserErrorStatus.*;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Bang9Exception.class)
    public ResponseEntity<Bang9Response<Void>> onThrowException(Bang9Exception bang9Exception, HttpServletRequest request) {

        ErrorReasonDto e = bang9Exception.getErrorReasonHttpStatus();

        log.error("Bang9Exception[{}] occurred: {}", e.getCode(), e.getMessage());

        return Bang9Response.onFailure(e.getCode(), e.getMessage(), e.getHttpStatus()).toResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", errorMessage);

        ResponseEntity<Bang9Response<Void>> apiResponse = Bang9Response.onFailure(VALIDATION_ERROR.getCode(), errorMessage, VALIDATION_ERROR.getHttpStatus()).toResponseEntity();
        return ResponseEntity.status(apiResponse.getStatusCode())
                .headers(headers)
                .body(apiResponse.getBody());
    }

    /**
     * Handle JWT token expired exceptions
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Bang9Response<Void>> handleExpiredJwtException(ExpiredJwtException ex, HttpServletRequest request) {
        log.debug("JWT token expired: {}", ex.getMessage());
        
        return Bang9Response.onFailure(
                TOKEN_EXPIRED.getCode(),
                TOKEN_EXPIRED.getMessage(),
                TOKEN_EXPIRED.getHttpStatus()
        ).toResponseEntity();
    }

    /**
     * Handle malformed JWT token exceptions
     */
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<Bang9Response<Void>> handleMalformedJwtException(MalformedJwtException ex, HttpServletRequest request) {
        log.debug("Malformed JWT token: {}", ex.getMessage());
        
        return Bang9Response.onFailure(
                INVALID_TOKEN.getCode(),
                INVALID_TOKEN.getMessage(),
                INVALID_TOKEN.getHttpStatus()
        ).toResponseEntity();
    }

    /**
     * Handle JWT signature validation exceptions
     */
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Bang9Response<Void>> handleSignatureException(SignatureException ex, HttpServletRequest request) {
        log.debug("Invalid JWT signature: {}", ex.getMessage());
        
        return Bang9Response.onFailure(
                INVALID_TOKEN.getCode(),
                INVALID_TOKEN.getMessage(),
                INVALID_TOKEN.getHttpStatus()
        ).toResponseEntity();
    }

    /**
     * Handle user not found during JWT authentication
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Bang9Response<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) {
        log.debug("User not found during JWT authentication: {}", ex.getMessage());
        
        return Bang9Response.onFailure(
                USER_NOT_FOUND.getCode(),
                USER_NOT_FOUND.getMessage(),
                USER_NOT_FOUND.getHttpStatus()
        ).toResponseEntity();
    }
}
