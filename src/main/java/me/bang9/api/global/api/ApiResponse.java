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
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    @JsonIgnore
    private final HttpStatus httpStatus;

    public ApiResponse(Boolean isSuccess, String code, String message, HttpStatus httpStatus) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public ApiResponse(Boolean isSuccess, String code, String message, T result, HttpStatus httpStatus) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.result = result;
        this.httpStatus = httpStatus;
    }

    // For success responses with response body
    public static <T> ApiResponse<T> onSuccess(String code, String message, T result) {
        return new ApiResponse<>(true, code, message, result, HttpStatus.OK);
    }

    // For success responses with response body and custom status
    public static <T> ApiResponse<T> onSuccess(String code, String message, T result, HttpStatus httpStatus) {
        return new ApiResponse<>(true, code, message, result, httpStatus);
    }

    // For success responses without response body
    public static ApiResponse<Void> onSuccess() {
        return new ApiResponse<>(true, CommonSuccessStatus._OK.getCode(), CommonSuccessStatus._OK.getMessage(), HttpStatus.OK);
    }

    // For failure responses
    public static ApiResponse<Void> onFailure(String code, String message, HttpStatus httpStatus) {
        return new ApiResponse<>(false, code, message, httpStatus);
    }

    public ResponseEntity<ApiResponse<T>> toResponseEntity() {
        return ResponseEntity.status(this.httpStatus).body(this);
    }

}