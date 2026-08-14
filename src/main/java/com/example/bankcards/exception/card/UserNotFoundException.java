package com.example.bankcards.exception.card;

import org.springframework.http.HttpStatus;

public final class UserNotFoundException extends CardManagementException {
    public UserNotFoundException() {
        super("User not found.", HttpStatus.NOT_FOUND);
    }
}
