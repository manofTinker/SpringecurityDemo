package com.lee.framework.security.strategy.password;

import com.lee.framework.security.bean.SecurityUserDetails;
import com.lee.framework.security.common.uitl.SecurityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultPasswordUserDetailsService extends AbstractPasswordUserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new SecurityUserDetails(username, "15888888888", "123456", SecurityUtils.getAuthorities());
    }
}
