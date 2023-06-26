package org.goodstorage.exceptions;

public class ValidationException extends ResponseStatusException {
    public ValidationException(String message) {
        super(400, message);
    }
}
