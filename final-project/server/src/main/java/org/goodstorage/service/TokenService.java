package org.goodstorage.service;

public interface TokenService {
    String generate(String username);

    void validate(String token);

    String validateAndGetUsername(String bearer);
}
