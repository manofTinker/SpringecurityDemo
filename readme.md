security是基于spring boot 2.2.x版本的安全认证组件，只支持前后端分离以json格式做数据交互的应用安全认证，包括两种认证模式

开启认证，您只需要完成以下步骤
一、在项目中引入pom依赖

    <dependency>
         <groupId>com.lee.framework</groupId>
         <artifactId>security</artifactId>
        <version>your version</version>
    </dependency>
    
二、在springboot 启动类上加上以下注解

    @ComponentScan(basePackages = "com.lee")
    @SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
    @EnableAutoSecurity
    public class DemoApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(DemoApplication.class, args);
        }
    
    }

以下介绍两种登录模式

**1.账号密码登录模式**

    您只要完成以下几个步骤就可以集成账号密码登录认证模式：
    a.（强制）继承com.lee.framework.security.strategy.password.AbstractPasswordUserDetailsService类，并重写loadUserByUsername方法，并让此类成为spring容器中的一个bean，托管于spring 容器；
    b.（可选）如果您应用的用户密码不是md5 encode方式，您需要实现org.springframework.security.crypto.password.PasswordEncoder，并让其成为spring容器中的一个bean;
    c.（强制）如果您需要定制化每次uri访问权限认证，您还需要实现com.lee.framework.security.AuthorityLoader接口，获取本次访问uri资源需要的权限集合，如果返回空的集合，则表示此次uri访问不需要任何授权，即可以匿名访问；

**2.短信验证码登录模式**    
   
    短信验证码登录的请求uri是com.lee.framework.security.common.SecurityConstants.SMS_CODE_LOGIN_PROCESSING_URI,短信验证码生成接口uri是com.lee.framework.security.common.SecurityConstants.SMS_CODE_GENERATE_URI
    
    您需要完成以下步骤就可以集成短信验证码登录认证模式
    a.（强制）继承com.lee.framework.security.strategy.sms.AbstractSmsCodeUserDetailsService，并重写并重写loadUserByUsername方法，并让此类成为spring容器中的一个bean，托管于spring 容器；
    b.如果您的应用需要定制化每次uri访问权限认证，请参考账号密码登录模式中步骤c的描述；
    c.（强制）在生产环境向短信运营商发送短信验证码，需要覆盖com.lee.framework.security.strategy.sms.AbstractSmsCodeSender#send方法，以完成自己的逻辑，如果是开发环境则可以自己配置，参考com.lee.framework.security.config.SecurityProperties#defaultValidateCode
 
 说明：在以上两种登录模式中，
 （可选）需要让您的应用更配置更灵活，您可以自定义您的配置，参考com.lee.framework.security.config.SecurityProperties，另外在用户登出的时候您需要做响应的处理的时候，你需要分别实现org.springframework.security.web.authentication.logout.LogoutHandler和org.springframework.security.web.authentication.logout.LogoutSuccessHandler中的方法
  
        
以下是可参考的yml配置信息及其注释
~~~~
security:
  config:
    # token签名key，不能为空
    signature: demoApp
    # token登录不需要做认证的url，多个url以英文逗号隔开
    authenticate-ignore-uri-patterns: /user/password/forgot
    # 密码模式登录请求uri
    user-login-process-uri: /user/login
    # token过期时间，单位天，超过此配置的token会自动失效，token失效后访问需要认证的资源需要登录认证
    token-expire-after-days: 30
    # 短信验证码默认长度
    sms-code-length: 6
    # 短信验证码发送成功后默认有效期，单位秒
    sms-code-expire-after-seconds: 120
    # 开发环境手机验证码登录时默认的验证码
    default-validate-code: 123
    # 除了手机验证码登录外还需要做手机验证码验证的url,多个uri以英文逗号分隔
    sms-code-validate-uri-patterns: /user/password/reset
