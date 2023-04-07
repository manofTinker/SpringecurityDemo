package com.lee.framework.security.strategy.onAuth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

public class RoleBasedVoter implements AccessDecisionVoter<Object> {

    /**
     * 访问权限决断放方法，只要拥有任意一种权限都可以放行，否则拒绝访问
     * @param authentication    用户认证信息，包含有用户拥有的权限
     * @param object            FilterInvocation
     * @param attributes        本次访问请求需要的权限集合
     */
    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        int access = ACCESS_ABSTAIN;
        // 本次访问如果不需要权限则放弃，否则要么授权访问要么拒绝访问
        if (CollectionUtils.isEmpty(attributes)) {
            return access;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (ConfigAttribute attribute : attributes) {
            String attr = attribute.getAttribute();
            if (StringUtils.isEmpty(attr)) {
                continue;
            }
            if (this.supports(attribute)) {
                access = ACCESS_DENIED;
                for (GrantedAuthority authority : authorities) {
                    if (StringUtils.equals(authority.getAuthority(), attr)) {
                        access = ACCESS_GRANTED;
                        break;
                    }
                }
            }
        }
        return access;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
