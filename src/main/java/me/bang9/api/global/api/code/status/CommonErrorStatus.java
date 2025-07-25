package me.bang9.api.global.api.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.bang9.api.global.api.code.BaseErrorCode;
import me.bang9.api.global.api.code.ErrorReasonDto;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorStatus implements BaseErrorCode {

    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-400", "Bad Request"),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON-401", "Unauthorized"),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON-403", "Forbidden"),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON-404", "Not Found"),
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-500", "Internal Server Error"),
    _SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "COMMON-503", "Service Unavailable");

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
