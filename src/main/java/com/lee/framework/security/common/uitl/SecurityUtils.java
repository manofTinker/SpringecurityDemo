package com.lee.framework.security.common.uitl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.framework.security.bean.AccessToken;
import com.lee.framework.security.bean.JwtBody;
import com.lee.framework.security.bean.SecurityUserDetails;
import com.lee.framework.security.bean.SimpleResponse;
import com.lee.framework.security.common.BaseErrorEnum;
import com.lee.framework.security.common.SecurityConstants;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SecurityUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private SecurityUtils() {}

    /**
     * 生成JWT
     *
     * @param subject    存入token中的subject
     * @param value      存入token中的subject的值
     * @param expireDays token过期时间
     * @return {@link String}
     */
    public static String getToken(String subject, Object value, Map<String, Object> additionalInfo, String signatureKey, int expireDays) {
        if (expireDays <= 0) {
            expireDays = defaultExpireDays();
        }
        int expireAfter = expireDays * 24 * 3600;
        Date issuedAt = new Date();
        String token = Jwts.builder()
                .setSubject(subject)
                .claim(SecurityConstants.AUTHORITIES, value)
                .addClaims(additionalInfo)
                .setExpiration(new Date(System.currentTimeMillis() + expireAfter * 1000L))
                .setIssuedAt(issuedAt)
                .signWith(SignatureAlgorithm.HS256, signatureKey)
                .compact();
        String key = getSessionKey(subject);
        if (CacheUtils.exists(key)) {
            CacheUtils.delete(key);
        }
        CacheUtils.set(key, DateUtil.format(issuedAt, DATE_FORMAT), expireAfter);
        return SecurityConstants.BEARER + token;
    }

    private static int defaultExpireDays() {
        return 30;
    }

    /**
     * 解析token获取subject信息
     *
     * @param token jwt对象 token
     * @return {@link String}
     */
    public static JwtBody resolve(String token, String signatureKey) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(signatureKey)
                    .parseClaimsJws(token.replace(SecurityConstants.BEARER, ""))
                    .getBody();
            Date issuedAt = claims.getIssuedAt();
            String subject = claims.getSubject();
            String issuedDate = CacheUtils.get(getSessionKey(subject));
            if (StringUtils.isEmpty(issuedDate) || !issuedDate.equals(DateUtil.format(issuedAt, DATE_FORMAT))) {
                if (logger.isDebugEnabled()) {
                    logger.debug("当前用户【{}】再一次登录，之前的登录将失效。", subject);
                }
                return null;
            }
            String userDetails = (String) claims.get(SecurityConstants.USER_INFO);
            JwtBody body = new JwtBody();
            body.setSubject(subject);
            body.setUserDetails(objectMapper.readValue(userDetails, SecurityUserDetails.class));
            return body;
        } catch (MalformedJwtException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("accessToken非法。");
            }
        } catch (ExpiredJwtException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("accessToken已经过期。");
            }
        } catch (SignatureException e1) {
            logger.warn("accessToken非法，需重新登录认证。");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return null;
    }

    /**
     * 响应带错误提示的json
     *
     * @param response      {@link HttpServletResponse}
     * @param baseErrorEnum 项目所有错误枚举 {@link BaseErrorEnum}
     */
    public static void out(HttpServletResponse response, BaseErrorEnum baseErrorEnum) {
        response.reset();
        PrintWriter out = null;
        try {
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            out = response.getWriter();
            out.println(JSONUtil.toJsonStr(SimpleResponse.error(baseErrorEnum)));
        } catch (Exception e) {
            logger.error("输出JSON出错。");
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 认证成功，向前端响应accessToken
     *
     * @param response {@link HttpServletResponse}
     * @param token    jwt令牌
     */
    public static void onAuthenticateSuccess(HttpServletResponse response, String token) {
        response.reset();
        PrintWriter out = null;
        try {
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            out = response.getWriter();
            out.println(JSONUtil.toJsonStr(SimpleResponse.success(build(token))));
        } catch (Exception e) {
            logger.error("输出JSON出错。");
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 根据用户的角色类型来初始化权限信息，如果是用户角色则只拥有用户权限，
     * 如果是代看管家则不但拥有用户角色，还拥有代看管家角色
     *
     * @return {@link GrantedAuthority}
     */
    public static List<GrantedAuthority> getAuthorities() {
        return translate(SecurityConstants.DEFAULT_AUTHORITIES);
    }

    public static List<GrantedAuthority> translate(String grantedAuthority) {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(grantedAuthority);
    }

    /**
     * 从线程中获取SecurityUserDetails对象，如果用户没有登录认证，则返回空
     *
     * @return {@link SecurityUserDetails}
     */
    public static SecurityUserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUserDetails userDetails = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            userDetails = (SecurityUserDetails) authentication.getPrincipal();
        }
        return userDetails;
    }

    /**
     * 用户登录唯一标识key，借助redis实现账户只能一人登录，同一个账户后面登录的用户会挤掉前面登录的账户
     *
     * @param subject 用户手机号
     * @return {@link String}
     */
    private static String getSessionKey(String subject) {
        return String.format(SecurityConstants.USER_SESSION_UNIQUE, subject);
    }

    /**
     * accessToken响应对象
     *
     * @param accessToken 访问令牌
     * @return {@link AccessToken}
     */
    private static AccessToken build(String accessToken) {
        return new AccessToken(accessToken);
    }


}
