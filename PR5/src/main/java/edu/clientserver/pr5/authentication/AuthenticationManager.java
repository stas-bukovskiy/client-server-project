package edu.clientserver.pr5.authentication;

import edu.clientserver.pr5.domain.User;
import edu.clientserver.pr5.exception.AuthenticationException;
import edu.clientserver.pr5.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
public class AuthenticationManager {

    private static final String secretKey = "amdsfpdOPFDmspmadfoapsdfpeasdkmcDlacMdpofpofsm";
    private static final long expirationTime = 3600000 * 24;

    private final UserRepository userRepository;


    public String authenticate(String username, String password) {
        try {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty())
                throw new AuthenticationException("Such user does not exist");

            if (user.get().getPassword().equals(password)) {
                return generateToken(username);
            } else {
                throw new AuthenticationException("Incorrect password");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public void validate(String token) {
        Jws<Claims> claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
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

}
