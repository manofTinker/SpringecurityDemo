package com.lee.framework.security;

import com.lee.framework.security.common.SecurityConstants;
import com.lee.framework.security.config.SecurityProperties;
import com.lee.framework.security.filter.JwtAuthenticationFilter;
import com.lee.framework.security.filter.SmsCodeValidateFilter;
import com.lee.framework.security.strategy.password.AbstractPasswordUserDetailsService;
import com.lee.framework.security.strategy.password.DefaultUsernamePasswordAuthenticationFilter;
import com.lee.framework.security.strategy.sms.SmsCodeAuthenticationSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.Set;


public class GlobalWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AbstractPasswordUserDetailsService passwordUserDetailsService;
    @Autowired
    private AuthenticateUrlMetadataSource authenticateUrlMetadataSource;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private LogoutHandler logoutHandler;
    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private AccessDecisionManager accessDecisionManager;
    @Autowired
    private FilterInvocationSecurityMetadataSource securityMetadataSource;
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    public DefaultUsernamePasswordAuthenticationFilter authenticationFilter() throws Exception {
        DefaultUsernamePasswordAuthenticationFilter filter = new DefaultUsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        filter.setFilterProcessesUrl(securityProperties.getUserLoginProcessUri());
        return filter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(passwordUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Set<String> authenticateIgnoreUrls = authenticateUrlMetadataSource.getAuthenticateIgnoreUrls();
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        for (String url : authenticateIgnoreUrls) {
            registry.antMatchers(url).permitAll();
        }
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager());
        jwtAuthenticationFilter.setAuthenticateUrlsConfiguration(authenticateUrlMetadataSource);
        jwtAuthenticationFilter.setSecurityProperties(securityProperties);

        SmsCodeValidateFilter smsCodeValidateFilter = new SmsCodeValidateFilter();
        smsCodeValidateFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        smsCodeValidateFilter.setAuthenticateUrlsSource(authenticateUrlMetadataSource);
        // 想过滤器链注册自定义账号密码登录filter，替换UsernamePasswordAuthenticationFilter
        registry.and()
                    .addFilterAt(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                    // 短信验证码放入过滤器链
                    .addFilterBefore(smsCodeValidateFilter, UsernamePasswordAuthenticationFilter.class)
                    .formLogin()
                    .loginPage(SecurityConstants.REQUIRED_AUTHENTICATION_URI)
                    .permitAll()
                .and()
                    .headers()
                    .frameOptions()
                    .disable()
                .and()
                    .logout()
                    .logoutUrl(SecurityConstants.LOGOUT_URI)
                    .logoutSuccessHandler(logoutSuccessHandler)
                    .addLogoutHandler(logoutHandler)
                    .permitAll()
                .and()
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                        @Override
                        public <O extends FilterSecurityInterceptor> O postProcess(O interceptor) {
                            interceptor.setAccessDecisionManager(accessDecisionManager);
                            interceptor.setSecurityMetadataSource(securityMetadataSource);
                            return interceptor;
                        }
                    })
                .and()
                    .exceptionHandling()
                    .accessDeniedHandler(accessDeniedHandler)
                .and()
                    .csrf()
                    .disable()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .addFilter(jwtAuthenticationFilter)
                    // 注册短信验证码方式登录配置
                    .apply(smsCodeAuthenticationSecurityConfig);
    }
}
