package com.lee.framework.security.exception;

import com.lee.framework.security.common.BaseErrorEnum;
import org.springframework.security.core.AuthenticationException;

public class ValidateCodeException extends AuthenticationException {

    private BaseErrorEnum baseErrorEnum;

    public ValidateCodeException(BaseErrorEnum baseErrorEnum) {
        super("");
        this.baseErrorEnum = baseErrorEnum;
    }
    public BaseErrorEnum getBaseErrorEnum() {
        return baseErrorEnum;
    }

    public void setBaseErrorEnum(BaseErrorEnum baseErrorEnum) {
        this.baseErrorEnum = baseErrorEnum;
    }
}
