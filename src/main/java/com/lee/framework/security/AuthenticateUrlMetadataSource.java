package com.lee.framework.security;

import com.lee.framework.security.common.SecurityConstants;
import com.lee.framework.security.config.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class AuthenticateUrlMetadataSource {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 访问不需要认证uri表达式
     */
    private static final Set<String> URL_IGNORE_LIST = new HashSet<>();

    /**
     * 访问需要短信验证码认证的路径表达式
     */
    private static final Set<String> SMS_VALIDATE_URL_LIST = new HashSet<>();

    static {

        // 初始化可以匿名访问的uri资源
        URL_IGNORE_LIST.add("/swagger-resources/**");
        URL_IGNORE_LIST.add( "/webjars/**");
        URL_IGNORE_LIST.add("/v2/api-docs");
        URL_IGNORE_LIST.add( "/doc.html");
        // 初始化需要短信验证码才可以访问的uri资源
        SMS_VALIDATE_URL_LIST.add(SecurityConstants.SMS_CODE_LOGIN_PROCESSING_URI);
        SMS_VALIDATE_URL_LIST.add(SecurityConstants.USER_PASSWORD_RESET_URI);
    }

    /**
     * 获取访问app后端资源不要做认证的url
     * @return  {@link HashSet}
     */
    public Set<String> getAuthenticateIgnoreUrls() {
        URL_IGNORE_LIST.add(SecurityConstants.SMS_CODE_GENERATE_URI);
        URL_IGNORE_LIST.add(SecurityConstants.PASSWORD_LOGIN_PROCESSING_URI);
        URL_IGNORE_LIST.add(SecurityConstants.REQUIRED_AUTHENTICATION_URI);
        String urls = securityProperties.getAuthenticateIgnoreUriPatterns();
        if (!StringUtils.isEmpty(urls)) {
            List<String> urlList = Stream.of(urls.split(",")).collect(Collectors.toList());
            for (String urlPattern : urlList) {
                if (!StringUtils.isEmpty(urlPattern.trim())) {
                    URL_IGNORE_LIST.add(urlPattern);
                }
            }
        }
        return URL_IGNORE_LIST;
    }

    /**
     * 获取需要经过短信验证码过滤器验证的url集合
     * @return  {@link HashSet}
     */
    public Set<String> getSmsValidateUrls() {
        String urls = securityProperties.getSmsCodeValidateUriPatterns();
        if (!StringUtils.isEmpty(urls)) {
            List<String> urlList = Stream.of(urls.split(",")).collect(Collectors.toList());
            for (String urlPattern : urlList) {
                if (!StringUtils.isEmpty(urlPattern.trim())) {
                    SMS_VALIDATE_URL_LIST.add(urlPattern);
                }
            }
        }
        return SMS_VALIDATE_URL_LIST;
    }

    /**
     * 判断当前uri是否是需要认证的uri
     * @param uri   {@link HttpServletRequest#getRequestURI()}
     * @return      true代表可以忽略认证，否则需要认证
     */
    public boolean isAuthenticateIgnoreUri(String uri) {
        boolean ignore = false;
        for (String url : getAuthenticateIgnoreUrls()) {
            if (pathMatcher.match(url, uri)) {
                ignore = true;
                break;
            }
        }
        return ignore;
    }

    /**
     * 判断当前uri是否需要经过短信验证码验证
     * @param uri   {@link HttpServletRequest#getRequestURI()}
     * @return      true代表需要短信验证码验证，否则不需要
     */
    public boolean isSmsValidateUrls(String uri) {
        boolean match = false;
        for (String url : getSmsValidateUrls()) {
            if (pathMatcher.match(url, uri)) {
                match = true;
                break;
            }
        }
        return match;
    }
}
