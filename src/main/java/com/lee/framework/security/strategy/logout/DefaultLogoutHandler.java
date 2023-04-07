package com.lee.framework.security.strategy.logout;

import com.lee.framework.security.config.SecurityProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultLogoutHandler implements LogoutHandler {

    private SecurityProperties securityProperties;


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // to do nothing

    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }
}
