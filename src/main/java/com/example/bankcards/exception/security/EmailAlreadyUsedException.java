package com.example.bankcards.exception.security;

import org.springframework.http.HttpStatus;

public final class EmailAlreadyUsedException extends AuthException {
    public EmailAlreadyUsedException() {
        super("Email is already used.", HttpStatus.BAD_REQUEST);
    }
}
