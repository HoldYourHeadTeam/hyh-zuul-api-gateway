
package com.uplan.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.compression.DefaultCompressionCodecResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public abstract class AbstractTokenResolver implements TokenResolver {

    protected static final String TIME_ZONE_CLAIM = "timeZone";

    @Value("${jwt.token.secret}")
    protected String secret;

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaimsBody(token);

            Date timeZonedExpirationTime = dateInClaimsTimeZone(claims);
            return !claims.getExpiration().before(timeZonedExpirationTime);

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT token {} is expired or invalid", token);
        }
        return false;
    }

    private Date dateInClaimsTimeZone(Claims claims) {
        String textTimeZoneId = (String) claims.get(TIME_ZONE_CLAIM);
        ZoneId zoneId = ZoneId.of(textTimeZoneId);

        TimeZone userTimeZone = TimeZone.getTimeZone(zoneId);
        return Calendar.getInstance(userTimeZone).getTime();
    }

    public Claims getClaimsBody(String token) {
        Jws<Claims> claims = getClaims(token);
        return claims.getBody();
    }

    protected Jws<Claims> getClaims(String token) {
        return Jwts.parser().setSigningKey(secret)
                .setCompressionCodecResolver(new DefaultCompressionCodecResolver())
                .parseClaimsJws(token);
    }

}
