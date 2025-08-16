package me.bang9.api.global.api.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.bang9.api.global.api.code.BaseErrorCode;
import me.bang9.api.global.api.code.ErrorReasonDto;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum CommonErrorStatus implements BaseErrorCode {

    _BAD_REQUEST(BAD_REQUEST, "COMMON-400", "Bad Request"),
    _UNAUTHORIZED(UNAUTHORIZED, "COMMON-401", "Unauthorized"),
    _FORBIDDEN(FORBIDDEN, "COMMON-403", "Forbidden"),
    _NOT_FOUND(NOT_FOUND, "COMMON-404", "Not Found"),
    _INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR, "COMMON-500", "Internal Server Error"),
    _SERVICE_UNAVAILABLE(SERVICE_UNAVAILABLE, "COMMON-503", "Service Unavailable"),

    ALREADY_DELETED(BAD_REQUEST, "COMMON-400", "Already deleted"),
    VALIDATION_ERROR(BAD_REQUEST, "VALIDATION-400", "Validation error");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return new ErrorReasonDto(
                false,
                code,
                message
        );
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return new ErrorReasonDto(
                httpStatus,
                false,
                code,
                message
        );
    }
}
