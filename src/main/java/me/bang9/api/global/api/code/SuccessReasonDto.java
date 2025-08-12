package me.bang9.api.global.api.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SuccessReasonDto {
    private HttpStatus httpStatus;

    private final boolean isSuccess;
    private final String code;
    private final String message;

    public SuccessReasonDto(boolean isSuccess, String code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    public SuccessReasonDto(HttpStatus httpStatus, boolean isSuccess, String code, String message) {
        this.httpStatus = httpStatus;
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
