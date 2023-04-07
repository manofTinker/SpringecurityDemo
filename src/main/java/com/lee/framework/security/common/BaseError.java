package com.lee.framework.security.common;

public class BaseError implements Error {

    private Integer code;

    private String message;


    public BaseError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseError(BaseErrorEnum baseErrorEnum) {
        this.code = baseErrorEnum.getCode();
        this.message = baseErrorEnum.getMessage();
    }

    public BaseError(String message) {
        this.code = BaseErrorEnum.BUSINESS_EXCEPTION.getCode();
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
