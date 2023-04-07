package com.lee.framework.security.filter;


import cn.hutool.json.JSONUtil;
import com.lee.framework.security.AuthenticateUrlMetadataSource;
import com.lee.framework.security.bean.ValidateCode;
import com.lee.framework.security.common.BaseErrorEnum;
import com.lee.framework.security.common.SecurityConstants;
import com.lee.framework.security.common.uitl.CacheUtils;
import com.lee.framework.security.exception.ValidateCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 短信验证码过滤器
 */
public class SmsCodeValidateFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private AuthenticationFailureHandler authenticationFailureHandler;

    private AuthenticateUrlMetadataSource authenticateUrlMetadataSource;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (isSmsValidateUrl(request.getRequestURI())) {
            try {
                validate(request);
            } catch (ValidateCodeException e) {
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * 判断当前请求uri是否需要做验证码验证
     * @param uri   请求uri
     * @return      需要返回true，否则返回false
     */
    private boolean isSmsValidateUrl(String uri) {
       return authenticateUrlMetadataSource.isSmsValidateUrls(uri);
    }

    /**
     * 校验短信验证码
     *
     * @param request   {@link HttpServletRequest}
     */
    private void validate(HttpServletRequest request) {
        String mobile = request.getParameter(SecurityConstants.MOBILE_PARAMETER);
        String smsCode = request.getParameter(SecurityConstants.SMS_CODE_PARAMETER);
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(smsCode)) {
            if (logger.isDebugEnabled()) {
                logger.debug("uri【{}】需要手机验证码认证。", request.getRequestURI());
            }
            throw new ValidateCodeException(BaseErrorEnum.PARAMETER_NOT_FOUND);
        }
        String key = String.format(SecurityConstants.VALIDATE_CODE, mobile);
        String validateCodeJson = CacheUtils.get(key);
        if (StringUtils.isEmpty(validateCodeJson)) {
            throw new ValidateCodeException(BaseErrorEnum.SMS_CODE_REQUIRED);
        }
        ValidateCode validateCode = JSONUtil.toBean(validateCodeJson, ValidateCode.class);
        if (validateCode == null) {
            throw new ValidateCodeException(BaseErrorEnum.SMS_CODE_REQUIRED);
        }
        if (validateCode.isExpired()) {
            throw new ValidateCodeException(BaseErrorEnum.SMS_CODE_EXPIRED);
        }
        String codeInRedis = validateCode.getCode();
        if (!smsCode.equals(codeInRedis)) {
            throw new ValidateCodeException(BaseErrorEnum.SMS_CODE_NOT_MATCH);
        }
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public void setAuthenticateUrlsSource(AuthenticateUrlMetadataSource authenticateUrlMetadataSource) {
        this.authenticateUrlMetadataSource = authenticateUrlMetadataSource;
    }
}
