package me.bang9.api.global.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.bang9.api.global.api.ApiResponse;
import me.bang9.api.global.api.code.ErrorReasonDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

import static me.bang9.api.global.api.code.status.CommonErrorStatus.VALIDATION_ERROR;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Bang9Exception.class)
    public ResponseEntity<ApiResponse<Void>> onThrowException(Bang9Exception bang9Exception, HttpServletRequest request) {

        ErrorReasonDto e = bang9Exception.getErrorReasonHttpStatus();

        log.error("Bang9Exception[{}] occurred: {}", e.getCode(), e.getMessage());

        return ApiResponse.onFailure(e.getCode(), e.getMessage(), e.getHttpStatus()).toResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", errorMessage);

        ResponseEntity<ApiResponse<Void>> apiResponse = ApiResponse.onFailure(VALIDATION_ERROR.getCode(), errorMessage, VALIDATION_ERROR.getHttpStatus()).toResponseEntity();
        return ResponseEntity.status(apiResponse.getStatusCode())
                .headers(headers)
                .body(apiResponse.getBody());
    }
}
