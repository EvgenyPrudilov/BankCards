package com.example.bankcards.exception.security;

import org.springframework.http.HttpStatus;

public final class BadCredentialsException extends AuthException {
    public BadCredentialsException() {
        super("Wrong user name or password.", HttpStatus.BAD_REQUEST);
    }
}
