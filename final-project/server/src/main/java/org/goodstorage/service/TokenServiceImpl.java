package org.goodstorage.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.goodstorage.exceptions.AuthenticationException;

import java.util.Date;

public class TokenServiceImpl implements TokenService {

    private static final String SECRET_KEY = "amdsfpdOPFDmspmadfoapsdfpeasdkmcDlacMdpofpofsm";
    private static final long EXPIRIATION_TIME = 3600000 * 24;

    @Override
    public String generate(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRIATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    @Override
    public void validate(String token) {
        Jws<Claims> claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            throw new AuthenticationException("Invalid token: " + e.getMessage());
        }
        Double expiration = claims.getBody().get("exp", Double.class);
        if (expiration == null)
            throw new AuthenticationException("Invalid token");
        if (expiration >= new Date().getTime())
            throw new AuthenticationException("Token is expired");
    }

    @Override
    public String validateAndGetUsername(String token) {
        Jws<Claims> claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);

            Double expiration = claims.getBody().get("exp", Double.class);
            if (expiration == null)
                throw new AuthenticationException("Invalid token");
            if (expiration >= new Date().getTime())
                throw new AuthenticationException("Token is expired");

            return claims.getBody().getSubject();
        } catch (Exception e) {
            throw new AuthenticationException("Invalid token: " + e.getMessage());
        }
    }
}
