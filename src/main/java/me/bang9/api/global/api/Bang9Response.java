package me.bang9.api.global.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import me.bang9.api.global.api.code.status.CommonSuccessStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class Bang9Response<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    @JsonIgnore
    private final HttpStatus httpStatus;

    public Bang9Response(Boolean isSuccess, String code, String message, HttpStatus httpStatus) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public Bang9Response(Boolean isSuccess, String code, String message, T result, HttpStatus httpStatus) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.result = result;
        this.httpStatus = httpStatus;
    }

    // For success responses without response body
    public static Bang9Response<Void> onSuccess() {
        return onSuccess(null);
    }

    // For success responses with response body
    public static <T> Bang9Response<T> onSuccess(T result) {
        return onSuccess(CommonSuccessStatus._OK.getCode(), CommonSuccessStatus._OK.getMessage(), result);
    }

    // For success responses with custom code, message, body
    public static <T> Bang9Response<T> onSuccess(String code, String message, T result) {
        return onSuccess(code, message, result, HttpStatus.OK);
    }

    // For success responses with custom code, message, body, status
    public static <T> Bang9Response<T> onSuccess(String code, String message, T result, HttpStatus httpStatus) {
        return new Bang9Response<>(true, code, message, result, httpStatus);
    }


    // For failure responses
    public static Bang9Response<Void> onFailure(String code, String message, HttpStatus httpStatus) {
        return new Bang9Response<>(false, code, message, httpStatus);
    }

    public ResponseEntity<Bang9Response<T>> toResponseEntity() {
        return ResponseEntity.status(this.httpStatus).body(this);
    }

}