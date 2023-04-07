package com.lee.framework.security.common;

public enum BaseErrorEnum implements Error {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),

    /**
     * 普通请求异常
     */
    BUSINESS_EXCEPTION(1000, "业务错误"),

    OBJECT_NOT_FOUND(10001, "请求数据不存在"),

    REQUEST_PARAMETER_EXCEPTION(10002, "请求参数错误"),
    PARAMETER_NOT_FOUND(10003, "必传参数缺失"),

    BAD_ACCOUNTS(10004, "手机号码有误，请重新输入"),
    BAD_CREDENTIALS(10005, "账号或密码有误，请重新输入"),
    ACCOUNT_LOCKED(10006, "账号已被锁定"),

    ACCOUNTS_DISABLED(10007, "账号不可用"),
    LOGIN_EXPIRED(10008, "登录已失效"),
    ACCESS_DENIED(10009, "权限不足"),
    ACCOUNT_EXPIRED(100010, "账号已过期"),

    REQUIRED_ROLE(10019, "角色权限不足"),

    SMS_CODE_REQUIRED(10025, "请发送短信验证码"),
    SMS_CODE_EXPIRED(10020, "短信验证码已过期"),
    SMS_CODE_NOT_MATCH(10021, "短信验证码不匹配"),
    VALIDATE_CODE_SEND_ERROR(10022, "短信验证码发送失败"),

    ACCESS_TOKEN_ERROR(10023, "令牌非法"),
    SUBJECT_DELETE(10024, "用户已注销"),

    FILE_TOO_LARGE_EXCEPTION(10017, "上传文件太大"),

    // 4000开头客户端异常
    REQUEST_NOT_SUPPORT(40001, "请求方式不支持"),
    NO_HANDLER_EXCEPTION(40002, "没有找到请求处理器"),

    // 5000开头服务端异常
    SERVER_ERROR(50001, "服务内部异常"),


    ;
    private final int code;

    private final String message;


    BaseErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
