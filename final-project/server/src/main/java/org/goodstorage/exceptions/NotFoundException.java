package org.goodstorage.exceptions;

public class NotFoundException extends ResponseStatusException {
    public NotFoundException(String item, String id) {
        super(404, String.format("Not found %s with id <%s>", item, id));
    }
}
