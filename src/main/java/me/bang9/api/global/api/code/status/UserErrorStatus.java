package me.bang9.api.global.api.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.bang9.api.global.api.code.BaseErrorCode;
import me.bang9.api.global.api.code.ErrorReasonDto;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorStatus implements BaseErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-404", "User not found"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER-409-01", "Email already exists"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "USER-409-02", "Nickname already exists"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER-400-01", "Invalid password format"),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "USER-400-02", "Invalid user role");

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
