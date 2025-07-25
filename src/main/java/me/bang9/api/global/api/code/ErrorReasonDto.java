package me.bang9.api.global.api.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorReasonDto {

    private HttpStatus httpStatus;
    private final boolean isSuccess;
    private final String code;
    private final String message;

    public ErrorReasonDto(boolean isSuccess, String code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    public ErrorReasonDto(HttpStatus httpStatus, boolean isSuccess, String code, String message) {
        this.httpStatus = httpStatus;
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}