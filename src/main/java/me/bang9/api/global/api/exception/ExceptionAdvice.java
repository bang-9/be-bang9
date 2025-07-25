package me.bang9.api.global.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.bang9.api.global.api.ApiResponse;
import me.bang9.api.global.api.code.ErrorReasonDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Bang9Exception.class)
    public ResponseEntity<ApiResponse<Object>> onThrowException(Bang9Exception bang9Exception, HttpServletRequest request) {

        ErrorReasonDto e = bang9Exception.getErrorReasonHttpStatus();

        log.error("Bang9Exception[{}] occurred: {}", e.getCode(), e.getMessage());

        return ApiResponse.onFailure(e.getCode(), e.getMessage(), e.getHttpStatus()).toResponseEntity();
    }

}
