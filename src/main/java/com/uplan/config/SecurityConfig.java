package com.uplan.config;

import brave.Tracing;
import com.uplan.filter.AuthSecurityFilter;
import com.uplan.security.impl.AccessTokenResolverImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ADMIN_ENDPOINT = "/api/v1/admin/**";
    private static final String LOGIN_ENDPOINT = "/uplan-auth-service/api/v1/**";

    private final AccessTokenResolverImpl jwtAccessTokenResolverImpl;
    private final Tracing tracing;

    public SecurityConfig(@Qualifier("accessTokenResolverImpl") AccessTokenResolverImpl jwtAccessTokenResolverImpl, Tracing tracing) {
        this.jwtAccessTokenResolverImpl = jwtAccessTokenResolverImpl;
        this.tracing = tracing;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .logout().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .addFilterAfter(new AuthSecurityFilter(tracing, jwtAccessTokenResolverImpl), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(LOGIN_ENDPOINT).permitAll()
                .antMatchers(ADMIN_ENDPOINT).hasRole("ADMIN")
                .anyRequest().authenticated();
    }

}