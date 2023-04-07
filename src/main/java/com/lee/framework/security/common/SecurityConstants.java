package com.lee.framework.security.common;

public class SecurityConstants {

    /**
     * token分割
     */
    public static final String BEARER = "Bearer ";

    /**
     * token参数头
     */
    public static final String ACCESS_TOKEN = "accessToken";

    /**
     * 权限参数头
     */
    public static final String AUTHORITIES = "authorities";


    public static final String AUTHORITIES_PREFIX = "ROLE_";

    /**
     * 登录成功后默认拥有的权限角色
     */
    public static final String DEFAULT_AUTHORITIES = AUTHORITIES_PREFIX + "admin";

    /**
     * 权限不足时跳转的路径，返回友好提示
     */
    public static final String REQUIRED_AUTHENTICATION_URI = "/authenticate/required";

    /**
     * 用户退出登录uri
     */
    public static final String LOGOUT_URI = "/user/logout";

    /**
     * 用户密码模式登录请求处理uri
     */
    public static final String PASSWORD_LOGIN_PROCESSING_URI = "/user/login";

    /**
     * 短信验证码模式登录请求处理uri
     */
    public static final String SMS_CODE_LOGIN_PROCESSING_URI = "/sms/login";

    /**
     * 短信验证码生成和发送接口
     */
    public static final String SMS_CODE_GENERATE_URI = "/sms/code/generate";

    /**
     * 密码设置uri
     */
    public static final String USER_PASSWORD_RESET_URI = "/user/password/reset";

    /**
     * 用户信息在jwt中key
     */
    public static final String USER_INFO = "userInfo";

    public static final String MOBILE_PARAMETER = "mobile";

    public static final String SMS_CODE_PARAMETER = "smsCode";

    public static final String USER_SESSION_UNIQUE = "user_%s";

    public static final String VALIDATE_CODE = "validate_code_%s";
            ;
}
