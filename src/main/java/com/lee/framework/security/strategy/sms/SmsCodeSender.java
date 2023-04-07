package com.lee.framework.security.strategy.sms;

import com.lee.framework.security.config.SecurityProperties;

public interface SmsCodeSender {

    /**
     * 手机验证码生成接口，如果是dev环境则直接生成配置的默认手机验证码，{@link SecurityProperties#defaultValidateCode}
     * 1.向手机发送验证码；
     * 2.手机验证码存入redis
     * @param mobile    接收验证码的手机号
     */
    void generate(String mobile);

}
