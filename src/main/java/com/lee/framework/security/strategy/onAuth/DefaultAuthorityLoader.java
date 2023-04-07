package com.lee.framework.security.strategy.onAuth;

import com.lee.framework.security.AuthorityLoader;
import com.lee.framework.security.common.uitl.SecurityUtils;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultAuthorityLoader implements AuthorityLoader {

    @Override
    public Set<String> loadAuthority(HttpServletRequest request) {
        return SecurityUtils.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }
}
