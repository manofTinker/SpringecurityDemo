package com.lee.framework.security.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class SecurityUserDetails implements UserDetails {

    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 密码
     */
    private String password;

    /**
     * 拥有的权限
     */
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * 账户是否没有过期
     */
    private boolean accountNonExpired;

    /**
     * 账户是否没被锁定
     */
    private boolean accountNonLocked;

    /**
     * 密码是否没过期
     */
    private boolean credentialsNonExpired;

    /**
     * 账户是否可用
     */
    private boolean enabled;

    public SecurityUserDetails() {

    }

    public SecurityUserDetails(String username, String mobile, String password, Collection<? extends GrantedAuthority> authorities) {
       this(username, mobile, password, authorities, true, true, true, true);
    }

    public SecurityUserDetails(String username, String mobile, String password,
                               Collection<? extends GrantedAuthority> authorities,
                               boolean enabled, boolean accountNonExpired,
                               boolean accountNonLocked, boolean credentialsNonExpired) {
        this.username = username;
        this.mobile = mobile;
        this.password = password;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    @JsonDeserialize(using = AuthorityDeserializer.class)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
