package com.lee.framework.security.exception;

import com.lee.framework.security.common.BaseError;
import com.lee.framework.security.common.BaseErrorEnum;

public class RestBusinessException extends RuntimeException {

    private BaseError baseError;

    public RestBusinessException(BaseErrorEnum baseErrorEnum) {
        super();
        this.baseError = new BaseError(baseErrorEnum);
    }

    public RestBusinessException(String message) {
        super();
        this.baseError = new BaseError(message);
    }

    public BaseError getBaseError() {
        return baseError;
    }

    public void setBaseError(BaseError baseError) {
        this.baseError = baseError;
    }
}
