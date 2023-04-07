package com.lee.framework.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


public class DefaultSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    private AuthorityLoader authorityLoader;

    private AuthenticateUrlMetadataSource authenticateUrlMetadataSource;

    /**
     * @param object  {@link FilterInvocation}
     * @return        返回本次访问需要的权限集合
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation invocation = (FilterInvocation) object;
        HttpServletRequest request = invocation.getHttpRequest();
        String uri = request.getRequestURI();
        if (authenticateUrlMetadataSource.isAuthenticateIgnoreUri(uri)) {
            if (logger.isDebugEnabled()) {
                logger.debug("请求【{}】可以匿名访问。", uri);
            }
            return new ArrayList<>();
        }
        Set<String> authorities = authorityLoader.loadAuthority(request);
        if (CollectionUtils.isEmpty(authorities)) {
            if (logger.isDebugEnabled()) {
                logger.debug("请求【{}】不需要授权即可访问。", uri);
            }
            return new ArrayList<>();
        }
        return authorities.stream().map(SecurityConfig::new).collect(Collectors.toList());
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    public void setAuthorityLoader(AuthorityLoader authorityLoader) {
        this.authorityLoader = authorityLoader;
    }

    public void setAuthenticateUrlsConfiguration(AuthenticateUrlMetadataSource authenticateUrlMetadataSource) {
        this.authenticateUrlMetadataSource = authenticateUrlMetadataSource;
    }
}
