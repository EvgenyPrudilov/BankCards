package com.example.bankcards.exception.security;

import org.springframework.http.HttpStatus;

public final class UserIsNotEnabledException extends AuthException {
    public UserIsNotEnabledException() {
        super("Confirm your password.", HttpStatus.BAD_REQUEST);
    }
}
