package com.lee.framework.security.filter;

import com.lee.framework.security.AuthenticateUrlMetadataSource;
import com.lee.framework.security.bean.JwtBody;
import com.lee.framework.security.bean.SecurityUserDetails;
import com.lee.framework.security.common.BaseErrorEnum;
import com.lee.framework.security.common.SecurityConstants;
import com.lee.framework.security.common.uitl.SecurityUtils;
import com.lee.framework.security.config.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt统一认证拦截过滤器
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SecurityProperties securityProperties;

    private AuthenticateUrlMetadataSource authenticateUrlMetadataSource;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationManager, authenticationEntryPoint);
    }

    /**
     * jwt认证逻辑
     * 1.不需要认证的请求uri未携带accessToken直接放行，线程上下文中用户认证信息为null，{@link SecurityUtils#getUserDetails()}
     * 2.不需要认证的请求uri已携带accessToken会解析token,解析成功后会将用户认证信息存入线程上下文，{@link SecurityUtils#getUserDetails()}
     * 3.需要认证的请求只会在认证授权成功后才能访问后端资源，否则会提示用户登录已失效，{@link BaseErrorEnum#LOGIN_EXPIRED}
     * @param request   {@link HttpServletRequest}
     * @param response  {@link HttpServletResponse}
     * @param chain     过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String accessToken = obtainAccessToken(request);
        if (StringUtils.isEmpty(accessToken)) {
            chain.doFilter(request, response);
            return;
        }
        String uri = request.getRequestURI();
        if (isAuthenticateIgnoreUri(uri)) {
            JwtBody body = SecurityUtils.resolve(accessToken, securityProperties.getSignature());
            if (body == null || StringUtils.isEmpty(body.getSubject())) {
                logger.warn("不需要认证的uri【" + uri + "】解析accessToken为空。");
                chain.doFilter(request, response);
                return;
            }
            UsernamePasswordAuthenticationToken authentication = getAuthenticationToken(body.getUserDetails());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
            return;
        }
        JwtBody body = SecurityUtils.resolve(accessToken, securityProperties.getSignature());
        if (body == null || StringUtils.isEmpty(body.getSubject())) {
            SecurityUtils.out(response, BaseErrorEnum.LOGIN_EXPIRED);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthenticationToken(body.getUserDetails());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    /**
     * 用解析得到的SecurityUserDetails对象，重新创建UsernamePasswordAuthenticationToken对象
     * @param securityUserDetails   统一认证user对象，{@link org.springframework.security.core.userdetails.UserDetails}
     * @return                      UsernamePasswordAuthenticationToken
     */
    private UsernamePasswordAuthenticationToken getAuthenticationToken(SecurityUserDetails securityUserDetails) {
        return new UsernamePasswordAuthenticationToken(securityUserDetails, null, securityUserDetails.getAuthorities());
    }

    /**
     * 判断当前请求uri是否是不需要经过认证的类型
     *
     * @param uri 请求uri
     * @return true则是可以匿名访问的uri，否则需要认证
     */
    private boolean isAuthenticateIgnoreUri(String uri) {
       return authenticateUrlMetadataSource.isAuthenticateIgnoreUri(uri);
    }

    /**
     * 从请求中获取accessToken请求参数的值
     *
     * @param request {@link HttpServletRequest}
     * @return 返回accessToken的值
     */
    private String obtainAccessToken(HttpServletRequest request) {
        return request.getHeader(SecurityConstants.ACCESS_TOKEN);
    }


    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public void setAuthenticateUrlsConfiguration(AuthenticateUrlMetadataSource authenticateUrlMetadataSource) {
        this.authenticateUrlMetadataSource = authenticateUrlMetadataSource;
    }
}
