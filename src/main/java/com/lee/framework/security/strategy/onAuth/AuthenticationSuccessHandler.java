package com.lee.framework.security.strategy.onAuth;

import com.lee.framework.security.bean.SecurityUserDetails;
import com.lee.framework.security.common.SecurityConstants;
import com.lee.framework.security.common.uitl.SecurityUtils;
import com.lee.framework.security.config.SecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();
        int expireDays = securityProperties.getTokenExpireAfterDays();
        String signature = securityProperties.getSignature();
        Map<String, Object> additionalInfo = getAdditionalInfo(userDetails);
        String token = SecurityUtils.getToken(userDetails.getMobile(), userDetails.getAuthorities(), additionalInfo, signature, expireDays);
        SecurityUtils.onAuthenticateSuccess(response, token);
    }

    /**
     * 在token中放入额外的用户信息，不存放密码
     * @param securityUserDetails   {@link SecurityUserDetails}
     * @return                      {@link HashMap}
     */
    private Map<String, Object> getAdditionalInfo(SecurityUserDetails securityUserDetails) {
        Map<String, Object> additionalInfo = new HashMap<>();
        securityUserDetails.setPassword("");
        try {
            String s = objectMapper.writeValueAsString(securityUserDetails);
            additionalInfo.put(SecurityConstants.USER_INFO, s);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return additionalInfo;
    }
}
