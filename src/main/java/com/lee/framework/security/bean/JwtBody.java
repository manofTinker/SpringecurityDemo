package com.lee.framework.security.bean;


public class JwtBody {

    private String subject;

    private SecurityUserDetails userDetails;


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public SecurityUserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(SecurityUserDetails userDetails) {
        this.userDetails = userDetails;
    }
}
