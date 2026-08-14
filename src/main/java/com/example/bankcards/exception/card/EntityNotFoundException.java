package com.example.bankcards.exception.card;

import org.springframework.http.HttpStatus;

public final class EntityNotFoundException extends CardManagementException {
    public EntityNotFoundException(String message) {
        super("EntityNotFoundException", HttpStatus.NOT_FOUND);
    }
}