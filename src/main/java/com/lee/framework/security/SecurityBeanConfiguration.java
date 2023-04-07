package com.lee.framework.security;

import com.lee.framework.security.config.SecurityProperties;
import com.lee.framework.security.encoder.MD5PasswordEncoder;
import com.lee.framework.security.strategy.logout.DefaultLogoutHandler;
import com.lee.framework.security.strategy.logout.DefaultLogoutSuccessHandler;
import com.lee.framework.security.strategy.onAuth.DefaultAccessDeniedHandler;
import com.lee.framework.security.strategy.onAuth.DefaultAuthorityLoader;
import com.lee.framework.security.strategy.onAuth.RoleBasedVoter;
import com.lee.framework.security.strategy.password.AbstractPasswordUserDetailsService;
import com.lee.framework.security.strategy.password.DefaultPasswordUserDetailsService;
import com.lee.framework.security.strategy.sms.AbstractSmsCodeUserDetailsService;
import com.lee.framework.security.strategy.sms.DefaultSmsCodeSender;
import com.lee.framework.security.strategy.sms.DefaultSmsCodeUserDetailsService;
import com.lee.framework.security.strategy.sms.SmsCodeSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityBeanConfiguration {

    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private AuthenticateUrlMetadataSource authenticateUrlMetadataSource;

    /**
     * 默认的账号密码登录模式，查询用户信息的UserDetailsService的默认实现
     *
     * @return {@link UserDetailsService}
     */
    @ConditionalOnMissingBean(AbstractPasswordUserDetailsService.class)
    @Bean
    public AbstractPasswordUserDetailsService passwordUserDetailsService() {
        return new DefaultPasswordUserDetailsService();
    }


    /**
     * 默认的手机短信验证码登录模式，查询用户信息的UserDetailsService默认bean
     *
     * @return {@link UserDetailsService}
     */
    @ConditionalOnMissingBean(AbstractSmsCodeUserDetailsService.class)
    @Bean
    public AbstractSmsCodeUserDetailsService smsCodeUserDetailsService() {
        return new DefaultSmsCodeUserDetailsService();
    }

    /**
     * 短信验证码发送默认实现，如果项目中用到了短信发送服务，需要子类重新实现{@link SmsCodeSender}
     *
     * @return 短信验证码生成和发送处理类
     */
    @ConditionalOnMissingBean(SmsCodeSender.class)
    @Bean
    public SmsCodeSender smsCodeSender() {
        return new DefaultSmsCodeSender();
    }

    /**
     * 默认登出处理器，如果容器中存在{@link LogoutHandler}的bean实例，则默认登录处理器不生效
     *
     * @return {@link LogoutHandler}
     */
    @ConditionalOnMissingBean(LogoutHandler.class)
    @Bean
    public LogoutHandler defaultLogoutHandler() {
        DefaultLogoutHandler defaultLogoutHandler = new DefaultLogoutHandler();
        defaultLogoutHandler.setSecurityProperties(securityProperties);
        return defaultLogoutHandler;
    }

    /**
     * 默认登出成功处理器，如果容器中存在{@link LogoutSuccessHandler}的bean实例，则默认登录成功处理器不生效
     *
     * @return {@link LogoutSuccessHandler}
     */
    @ConditionalOnMissingBean(LogoutSuccessHandler.class)
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        DefaultLogoutSuccessHandler defaultLogoutSuccessHandler = new DefaultLogoutSuccessHandler();
        defaultLogoutSuccessHandler.setSecurityProperties(securityProperties);
        return defaultLogoutSuccessHandler;
    }

    /**
     * 账号密码登录模式密码加密实现，默认是md5加密，当容器存在{@link PasswordEncoder}的实例时,
     * 则不实例化默认的md5加密，用户可以定制化加密方式
     *
     * @return {@link PasswordEncoder}
     */
    @ConditionalOnMissingBean(PasswordEncoder.class)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MD5PasswordEncoder();
    }

    /**
     * 权限动态加载bean，返回通过请求uri加载可以访问此uri需要的所有权限，如果开发者自己定义了动态权限加载实现，默认bean将不会生效
     * @return  {@link AuthorityLoader}
     */
    @ConditionalOnMissingBean(AuthorityLoader.class)
    @Bean
    public AuthorityLoader authorityLoader() {
        return new DefaultAuthorityLoader();
    }

    /**
     * 自定义权限认证失败处理器，默认是响应json提示权限不足，{@link com.lee.framework.security.common.BaseErrorEnum#ACCESS_DENIED}
     * @return  {@link AccessDeniedHandler}
     */
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new DefaultAccessDeniedHandler();
    }

    /**
     * 拓展权限决断处理器，基于表达式以及自定义角色权限投票规则
     * @return  {@link AccessDecisionManager}
     */
    @Bean
    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();
        decisionVoters.add(new WebExpressionVoter());
        decisionVoters.add(new RoleBasedVoter());
        return new AffirmativeBased(decisionVoters);
    }

    @Bean
    public FilterInvocationSecurityMetadataSource securityMetadataSource(AuthorityLoader authorityLoader) {
        DefaultSecurityMetadataSource securityMetadataSource = new DefaultSecurityMetadataSource();
        securityMetadataSource.setAuthorityLoader(authorityLoader);
        securityMetadataSource.setAuthenticateUrlsConfiguration(authenticateUrlMetadataSource);
        return securityMetadataSource;
    }
}
