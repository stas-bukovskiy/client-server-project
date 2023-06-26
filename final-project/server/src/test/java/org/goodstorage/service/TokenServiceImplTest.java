package org.goodstorage.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.goodstorage.exceptions.AuthenticationException;
import org.goodstorage.util.RandomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceImplTest {

    private static final String SECRET_KEY = "amdsfpdOPFDmspmadfoapsdfpeasdkmcDlacMdpofpofsm";
    private static final long EXPIRATION_TIME = 3600000 * 24;

    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl();
    }

    @Test
    void generate_ShouldReturnValidToken() {
        String username = RandomUtil.randomString(10);
        String token = tokenService.generate(username);

        assertNotNull(token);
        assertDoesNotThrow(() -> Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token));
    }

    @Test
    void validate_ValidToken_ShouldNotThrowException() {
        String token = generateValidToken(RandomUtil.randomString(10));

        assertDoesNotThrow(() -> tokenService.validate(token));
    }

    @Test
    void validate_InvalidToken_ShouldThrowAuthenticationException() {
        String token = "invalidToken";

        assertThrows(AuthenticationException.class, () -> tokenService.validate(token));
    }

    @Test
    void validate_ExpiredToken_ShouldThrowAuthenticationException() {
        String token = generateExpiredToken();

        assertThrows(AuthenticationException.class, () -> tokenService.validate(token));
    }

    @Test
    void validateAndGetUsername_ValidToken_ShouldReturnUsername() {
        String username = RandomUtil.randomString(10);
        ;
        String token = generateValidToken(username);

        String result = tokenService.validateAndGetUsername(token);

        assertEquals(username, result);
    }

    @Test
    void validateAndGetUsername_InvalidToken_ShouldThrowAuthenticationException() {
        String token = "invalidToken";

        assertThrows(AuthenticationException.class, () -> tokenService.validateAndGetUsername(token));
    }

    @Test
    void validateAndGetUsername_ExpiredToken_ShouldThrowAuthenticationException() {
        String token = generateExpiredToken();

        assertThrows(AuthenticationException.class, () -> tokenService.validateAndGetUsername(token));
    }

    private String generateValidToken(String string) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(string)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    private String generateExpiredToken() {
        Date now = new Date();
        Date expiration = new Date(now.getTime() - 1000);

        return Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
