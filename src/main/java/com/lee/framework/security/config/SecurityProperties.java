package com.lee.framework.security.config;

import com.lee.framework.security.common.SecurityConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.config")
public class SecurityProperties {

    /**
     * token生成和解析时签名
     */
    private String signature = "lee-security";

    /**
     * accessToken过期时间，单位是天
     */
    private int tokenExpireAfterDays = 30;

    /**
     * 不需要经过认证的url表达式
     */
    private String authenticateIgnoreUriPatterns = "";

    /**
     * 用户账号密码模式登录uri
     */
    private String userLoginProcessUri = SecurityConstants.PASSWORD_LOGIN_PROCESSING_URI;

    /**
     * 需要短信验证码校验的url表达式
     */
    private String smsCodeValidateUriPatterns;

    /**
     * 开发环境默认的短信验证码
     */
    private String defaultValidateCode = "123456";

    /**
     * 生成短信验证码的长度
     */
    private int SmsCodeLength = 6;

    /**
     * 短信验证码过期时间，单位秒
     */
    private int smsCodeExpireAfterSeconds = 100;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getTokenExpireAfterDays() {
        return tokenExpireAfterDays;
    }

    public void setTokenExpireAfterDays(int tokenExpireAfterDays) {
        this.tokenExpireAfterDays = tokenExpireAfterDays;
    }

    public String getAuthenticateIgnoreUriPatterns() {
        return authenticateIgnoreUriPatterns;
    }

    public void setAuthenticateIgnoreUriPatterns(String authenticateIgnoreUriPatterns) {
        this.authenticateIgnoreUriPatterns = authenticateIgnoreUriPatterns;
    }

    public String getUserLoginProcessUri() {
        return userLoginProcessUri;
    }

    public void setUserLoginProcessUri(String userLoginProcessUri) {
        this.userLoginProcessUri = userLoginProcessUri;
    }

    public String getSmsCodeValidateUriPatterns() {
        return smsCodeValidateUriPatterns;
    }

    public void setSmsCodeValidateUriPatterns(String smsCodeValidateUriPatterns) {
        this.smsCodeValidateUriPatterns = smsCodeValidateUriPatterns;
    }

    public String getDefaultValidateCode() {
        return defaultValidateCode;
    }

    public void setDefaultValidateCode(String defaultValidateCode) {
        this.defaultValidateCode = defaultValidateCode;
    }

    public int getSmsCodeLength() {
        return SmsCodeLength;
    }

    public void setSmsCodeLength(int smsCodeLength) {
        SmsCodeLength = smsCodeLength;
    }

    public int getSmsCodeExpireAfterSeconds() {
        return smsCodeExpireAfterSeconds;
    }

    public void setSmsCodeExpireAfterSeconds(int smsCodeExpireAfterSeconds) {
        this.smsCodeExpireAfterSeconds = smsCodeExpireAfterSeconds;
    }
}
