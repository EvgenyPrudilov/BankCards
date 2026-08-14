package com.example.bankcards.exception.card;

import org.springframework.http.HttpStatus;

public final class UserAlreadyExistsException extends CardManagementException {
    public UserAlreadyExistsException() {
        super("User with this name already exists.", HttpStatus.BAD_REQUEST);
    }
}
