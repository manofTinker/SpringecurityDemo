package com.lee.framework.security.common;

public interface Error {

    /**
     * 统一响应码
     */
    int getCode();

    /**
     * 统一响应信息
     */
    String getMessage();

}
