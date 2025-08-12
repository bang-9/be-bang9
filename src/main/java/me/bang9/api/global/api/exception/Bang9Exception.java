package me.bang9.api.global.api.exception;

import me.bang9.api.global.api.code.BaseErrorCode;
import me.bang9.api.global.api.code.ErrorReasonDto;

public class Bang9Exception extends RuntimeException {

    private final BaseErrorCode code;

    public Bang9Exception(BaseErrorCode code) {
        this.code = code;
    }

    public ErrorReasonDto getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
