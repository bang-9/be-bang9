package me.bang9.api.global.api.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.bang9.api.global.api.code.BaseSuccessCode;
import me.bang9.api.global.api.code.SuccessReasonDto;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonSuccessStatus implements BaseSuccessCode {

    _OK(HttpStatus.OK, "COMMON-200", "Request was successful"),
    _CREATED(HttpStatus.CREATED, "COMMON-201", "Resource was created successfully");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public SuccessReasonDto getReason() {
        return new SuccessReasonDto(
                true,
                code,
                message
        );
    }

    @Override
    public SuccessReasonDto getReasonHttpStatus() {
        return new SuccessReasonDto(
                httpStatus,
                true,
                code,
                message
        );
    }
}
