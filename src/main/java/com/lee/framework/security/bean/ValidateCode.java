package com.lee.framework.security.bean;

import java.time.LocalDateTime;

public class ValidateCode {

    /**
     * 验证码
     */
    private String code;

    /**
     * 过期时间点
     */
    private LocalDateTime expireAt;


    public ValidateCode() {

    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireAt);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

}
