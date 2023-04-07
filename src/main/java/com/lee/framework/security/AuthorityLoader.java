package com.lee.framework.security;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

public interface AuthorityLoader {

    /**
     * 通过请求加载可以访问此uri需要的所有权限，返回空则表示访问任何uri都不需要做权限认证
     * @param request   {@link HttpServletRequest}
     * @return      权限集合
     */
    Set<String> loadAuthority(HttpServletRequest request);

}
