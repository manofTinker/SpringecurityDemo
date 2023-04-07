package com.lee.framework.security.strategy.onAuth;

import com.lee.framework.security.common.BaseErrorEnum;
import com.lee.framework.security.common.uitl.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        SecurityUtils.out(response, BaseErrorEnum.ACCESS_DENIED);
    }
}
