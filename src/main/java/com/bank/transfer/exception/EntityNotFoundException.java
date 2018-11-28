package com.bank.transfer.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, String entityId) {
        super(String.format("%s with id '%s' not exist", entityName, entityId));
    }
}
