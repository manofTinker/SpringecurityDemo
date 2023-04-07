package com.lee.framework.security.strategy.sms;

import com.lee.framework.security.bean.SecurityUserDetails;
import com.lee.framework.security.common.uitl.SecurityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultSmsCodeUserDetailsService extends AbstractSmsCodeUserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String mobile) throws UsernameNotFoundException {
        return new SecurityUserDetails("", mobile, "", SecurityUtils.getAuthorities());
    }
}
