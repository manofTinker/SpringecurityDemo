package com.lee.framework.security.bean;

public class AccessToken {

    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public AccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String MyMethodTest() {
        return "a";
    }

    public String MyMethodTest2() {
        return "a";
    }
}
