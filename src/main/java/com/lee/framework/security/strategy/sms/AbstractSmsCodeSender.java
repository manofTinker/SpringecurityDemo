package com.lee.framework.security.strategy.sms;

import cn.hutool.json.JSONUtil;
import com.lee.framework.security.bean.ValidateCode;
import com.lee.framework.security.common.SecurityConstants;
import com.lee.framework.security.common.uitl.CacheUtils;
import com.lee.framework.security.config.SecurityProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 手机验证码生成类抽象实现
 */
public abstract class AbstractSmsCodeSender implements SmsCodeSender {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SecurityProperties securityProperties;

    @Value("${spring.profiles.active:dev}")
    private String profile;

    @Override
    public final void generate(String mobile) {
        String smsCode = securityProperties.getDefaultValidateCode();
        if (!StringUtils.isEmpty(profile) && "prd".equals(profile)) {
            smsCode = RandomStringUtils.randomNumeric(securityProperties.getSmsCodeLength());
            send(mobile, smsCode);
            logger.info("向手机号【{}】成功发送短信验证码【{}】。", mobile, smsCode);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("当前环境读取【{}】配置，生成验证码【{}】", profile, smsCode);
        }
        int smsCodeExpireAfterSeconds = securityProperties.getSmsCodeExpireAfterSeconds();
        ValidateCode validateCode = new ValidateCode();
        validateCode.setCode(smsCode);
        validateCode.setExpireAt(LocalDateTime.now().plusSeconds(smsCodeExpireAfterSeconds));
        String key = String.format(SecurityConstants.VALIDATE_CODE, mobile);
        CacheUtils.set(key, JSONUtil.toJsonStr(validateCode), smsCodeExpireAfterSeconds * 2);
    }

    /**
     * 为手机号发送验证码，子类必须实现短信验证码发送的逻辑
     *
     * @param mobile  手机号
     * @param smsCode 验证码
     */
    protected abstract void send(String mobile, String smsCode);
}
