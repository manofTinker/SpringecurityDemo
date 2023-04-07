package com.lee.framework.security.strategy.sms;

public class DefaultSmsCodeSender extends AbstractSmsCodeSender {

    @Override
    protected void send(String mobile, String smsCode) {
        throw new RuntimeException("no SMS operator found");
    }
}
