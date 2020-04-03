package com.uplan.filter;

import brave.Tracing;
import brave.propagation.ExtraFieldPropagation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uplan.security.ContextUser;
import com.uplan.security.TokenResolver;
import com.uplan.security.impl.JwtUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
public class AuthSecurityFilter extends OncePerRequestFilter {

    private static final String SLEUTH_CONTEXT_USER_KEY = "context-user";

    private Tracing tracing;

    private TokenResolver tokenResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = tokenResolver.resolveToken(request);
        if (!StringUtils.isEmpty(token) && tokenResolver.isTokenValid(token)) {
            putValidTokenToContext(token);
        }
        doFilter(request, response, filterChain);
    }

    private void putValidTokenToContext(String token) {
        Authentication auth = tokenResolver.getAuthentication(token);
        if (auth != null) {
            injectToSleuthContext(auth);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }

    private void injectToSleuthContext(Authentication auth) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ContextUser contextUser = new ContextUser((JwtUser)auth.getPrincipal());
            String jsonContextUser = objectMapper.writeValueAsString(contextUser);

            ExtraFieldPropagation.set(tracing.currentTraceContext().get(), SLEUTH_CONTEXT_USER_KEY, jsonContextUser);
        } catch (JsonProcessingException ex) {
            log.error("Failed during json serialization in auth filer",ex);
        }
    }

}
