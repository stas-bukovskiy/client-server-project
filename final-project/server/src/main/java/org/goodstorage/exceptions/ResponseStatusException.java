package org.goodstorage.exceptions;

import lombok.Getter;

@Getter
public class ResponseStatusException extends RuntimeException {

    private final int statusCode;

    public ResponseStatusException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ResponseStatusException(int statusCode, Throwable cause) {
        super(cause);
        this.statusCode = statusCode;
    }
}
