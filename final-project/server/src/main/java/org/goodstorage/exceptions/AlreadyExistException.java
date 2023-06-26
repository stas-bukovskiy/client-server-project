package org.goodstorage.exceptions;

public class AlreadyExistException extends ResponseStatusException {

    public static final String SQL_ALREADY_EXISTS_STATE = "23505";

    public AlreadyExistException(String message, Object... params) {
        super(409, String.format(message, params));
    }

}
