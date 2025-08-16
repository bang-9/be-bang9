package me.bang9.api.global.api.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.bang9.api.global.api.code.BaseErrorCode;
import me.bang9.api.global.api.code.ErrorReasonDto;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum UserErrorStatus implements BaseErrorCode {

    USER_NOT_FOUND(NOT_FOUND, "USER-404", "User not found"),
    DUPLICATE_EMAIL(CONFLICT, "USER-409-01", "Email already exists"),
    DUPLICATE_NICKNAME(CONFLICT, "USER-409-02", "Nickname already exists"),
    INVALID_PASSWORD(BAD_REQUEST, "USER-400-01", "Invalid password format"),
    INVALID_USER_ROLE(BAD_REQUEST, "USER-400-02", "Invalid user role"),

    // JWT Authentication related errors
    INVALID_CREDENTIALS(UNAUTHORIZED, "AUTH-401-01", "Invalid email or password"),
    INVALID_TOKEN(UNAUTHORIZED, "AUTH-401-02", "Invalid JWT token"),
    TOKEN_EXPIRED(UNAUTHORIZED, "AUTH-401-03", "JWT token expired"),
    REFRESH_TOKEN_EXPIRED(UNAUTHORIZED, "AUTH-401-04", "Refresh token expired"),
    UNAUTHORIZED_ACCESS(UNAUTHORIZED, "AUTH-401-05", "Unauthorized access"),
    USER_ACCOUNT_DISABLED(UNAUTHORIZED, "AUTH-401-06", "User account is disabled");

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
