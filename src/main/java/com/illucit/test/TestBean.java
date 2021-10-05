package com.illucit.test;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@Named("test")
@RequestScoped
public class TestBean {

    @Inject
    private HttpServletRequest request;

    public String getUserAgent() {
        return request.getHeader("User-Agent");
    }

}
