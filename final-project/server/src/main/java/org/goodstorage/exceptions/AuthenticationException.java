package org.goodstorage.exceptions;

public class AuthenticationException extends ResponseStatusException {
    public AuthenticationException(String message) {
        super(401, message);
    }
}
