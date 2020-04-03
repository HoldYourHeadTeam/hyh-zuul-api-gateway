package com.hyh.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface TokenResolver {

    /**
     * Method that parse string token to object representation. It allows to get details about token owner.In addition
     * authentication object holds information such as creation and expiration creationTime.
     *
     * @param token - unique session token that will be parsed.
     * @return - object that represents user and additional token information.
     */
    Authentication getAuthentication(String token);

    Claims getClaimsBody(String token);

    /**
     * Method obtains token string from request.
     *
     * @param request - request from with token will be obtained.
     * @return - string session token or null if request doesn't have token attribute.
     */
    String resolveToken(HttpServletRequest request);

    /**
     * Check is token valid e.g. verify expiration creationTime or token pattern.
     *
     * @param token - token that will be checked.
     * @return - true if token is valid and false - if not.
     */
    boolean isTokenValid(String token);

}
