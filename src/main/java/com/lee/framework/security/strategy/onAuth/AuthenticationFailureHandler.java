package com.lee.framework.security.strategy.onAuth;

import com.lee.framework.security.common.BaseErrorEnum;
import com.lee.framework.security.common.uitl.SecurityUtils;
import com.lee.framework.security.exception.LogoutException;
import com.lee.framework.security.exception.ValidateCodeException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof UsernameNotFoundException) {
            SecurityUtils.out(response, BaseErrorEnum.BAD_ACCOUNTS);
        } else if (exception instanceof BadCredentialsException) {
            SecurityUtils.out(response, BaseErrorEnum.BAD_CREDENTIALS);
        } else if (exception instanceof LockedException) {
            SecurityUtils.out(response, BaseErrorEnum.ACCOUNT_LOCKED);
        } else if (exception instanceof DisabledException) {
            SecurityUtils.out(response, BaseErrorEnum.ACCOUNTS_DISABLED);
        } else if (exception instanceof AccountExpiredException) {
            SecurityUtils.out(response, BaseErrorEnum.ACCOUNT_EXPIRED);
        } else if (exception instanceof ValidateCodeException) {
            ValidateCodeException validateCodeException = (ValidateCodeException) exception;
            SecurityUtils.out(response, validateCodeException.getBaseErrorEnum());
        } else if (exception instanceof LogoutException) {
            SecurityUtils.out(response, BaseErrorEnum.SUBJECT_DELETE);
        } else {
            SecurityUtils.out(response, BaseErrorEnum.SERVER_ERROR);
        }
    }
}
