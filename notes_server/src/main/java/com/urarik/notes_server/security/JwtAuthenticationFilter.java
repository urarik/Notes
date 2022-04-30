package com.urarik.notes_server.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtAuthenticationService jwtAuthenticationService;

    public JwtAuthenticationFilter(JwtAuthenticationService jwtAuthenticationService) {
        this.jwtAuthenticationService = jwtAuthenticationService;
    }

    // fetches the Authentication object using the getAuthentication()
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        Authentication authentication = jwtAuthenticationService.getAuthentication((HttpServletRequest)request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Causes the next filter in the chain to be invoked
        filterChain.doFilter(request, response);
    }
}
