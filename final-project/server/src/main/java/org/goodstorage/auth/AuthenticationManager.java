package org.goodstorage.auth;


import com.sun.net.httpserver.HttpExchange;
import lombok.RequiredArgsConstructor;
import org.goodstorage.domain.User;
import org.goodstorage.exceptions.AuthenticationException;
import org.goodstorage.repository.UserRepository;
import org.goodstorage.service.TokenService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class AuthenticationManager {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public void validateAuthentication(HttpExchange exchange) {
        List<String> authorization = exchange.getRequestHeaders().get("Authorization");
        if (authorization == null || authorization.size() == 0) {
            throw new AuthenticationException("Header does not contain 'Authorization'");
        }
        tokenService.validate(authorization.get(0).replace("Bearer ", ""));
    }

    public User validateAuthenticationAndGetUser(HttpExchange exchange) {
        List<String> authorization = exchange.getRequestHeaders().get("Authorization");
        if (authorization == null || authorization.size() == 0) {
            throw new AuthenticationException("Header does not contain 'Authorization'");
        }
        String username = tokenService.validateAndGetUsername(authorization.get(0).replace("Bearer ", ""));
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Can not find user with username '" + username + "'"));
    }


    public String authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty())
            throw new AuthenticationException("Such user does not exist");

        if (user.get().getPassword().equals(password)) {
            return tokenService.generate(username);
        } else {
            throw new AuthenticationException("Incorrect password");
        }
    }

}
