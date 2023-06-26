package org.goodstorage.exceptions;

public class DatabaseException extends ResponseStatusException {
    public DatabaseException(String message) {
        super(500, message);
    }

    public DatabaseException(Throwable cause) {
        super(500, cause);
    }
}
