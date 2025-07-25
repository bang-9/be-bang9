package me.bang9.api.global.api.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.bang9.api.global.api.code.BaseErrorCode;
import me.bang9.api.global.api.code.ErrorReasonDto;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AgencyErrorStatus implements BaseErrorCode {

    AGENCY_NOT_FOUND(HttpStatus.NOT_FOUND, "AGENCY-404", "Agency not found"),
    INVALID_REPRESENTATIVE(HttpStatus.BAD_REQUEST, "AGENCY-400-01", "Invalid representative user"),
    DUPLICATE_AGENCY_EMAIL(HttpStatus.CONFLICT, "AGENCY-409", "Agency email already exists"),
    AGENCY_CREATION_FAILED(HttpStatus.BAD_REQUEST, "AGENCY-400-02", "Agency creation failed");

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
