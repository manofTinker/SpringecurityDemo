package com.lee.framework.security.controller;

import com.lee.framework.security.bean.SimpleResponse;
import com.lee.framework.security.common.BaseErrorEnum;
import com.lee.framework.security.common.SecurityConstants;
import com.lee.framework.security.exception.RestBusinessException;
import com.lee.framework.security.strategy.sms.SmsCodeSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController
public class SecurityController {

    // 手机号
    public static final String MOBILE = "^[1](([3|5|8][\\d])|([4][4,5,6,7,8,9])|([6][2,5,6,7])|([7][^9])|([9][1,8,9]))[\\d]{8}$";

    public static Pattern MOBILE_PATTERN;

    static {
        MOBILE_PATTERN = Pattern.compile(MOBILE);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SmsCodeSender smsCodeSender;

    /**
     * 当权限不足时，跳转响应
     * @return  统一响应对象，{@link SimpleResponse}
     */
    @RequestMapping(value = SecurityConstants.REQUIRED_AUTHENTICATION_URI)
    public SimpleResponse<BaseErrorEnum> require() {
        return SimpleResponse.error(BaseErrorEnum.ACCESS_DENIED);
    }

    /**
     * 短信验证码生成接口
     * @param mobile    生成手机验证码的手机号
     * @return          统一响应对象，{@link SimpleResponse}
     */
    @RequestMapping(value = SecurityConstants.SMS_CODE_GENERATE_URI)
    public SimpleResponse<Object> generate(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            logger.warn("手机验证码生成接口请求参数缺失。");
            throw new RestBusinessException(BaseErrorEnum.PARAMETER_NOT_FOUND);
        }
        if (!MOBILE_PATTERN.matcher(mobile).matches()) {
            logger.warn("手机验证码生成接口请求参数错误。");
            throw new RestBusinessException(BaseErrorEnum.REQUEST_PARAMETER_EXCEPTION);
        }
        smsCodeSender.generate(mobile);
        return SimpleResponse.success();
    }

}
