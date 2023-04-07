package com.lee.framework.security.bean;

import com.lee.framework.security.common.BaseErrorEnum;

public class SimpleResponse<E> {

    // 响应错误码
    private int code;

    // 服务端是否正确响应
    private boolean success;

    // 错误响应时的消息提示
    private String message;

    // 正确响应时的数据对象
    private E data;

    private SimpleResponse() {
        this.code = BaseErrorEnum.SUCCESS.getCode();
        this.success = true;
        this.message = BaseErrorEnum.SUCCESS.getMessage();
    }

    private SimpleResponse(int code, String message, boolean success) {
        this.code = code;
        this.success = success;
        this.message = message;
    }

    private SimpleResponse(E data) {
        this();
        this.data = data;
    }

    public static <E> SimpleResponse<E> success() {
        return new SimpleResponse<>();
    }

    public static <E> SimpleResponse<E> success(E data) {
        return new SimpleResponse<>(data);
    }

    public static <E> SimpleResponse<E> error(int code, String message, boolean success) {
        return new SimpleResponse<>(code, message, success);
    }

    public static <E> SimpleResponse<E> error(BaseErrorEnum baseErrorEnum) {
        return error(baseErrorEnum.getCode(), baseErrorEnum.getMessage(), false);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }
}
