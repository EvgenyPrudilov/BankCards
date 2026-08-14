package com.example.bankcards.exception.security;

import org.springframework.http.HttpStatus;

public final class RefreshTokenNotFoundException extends AuthException {
    public RefreshTokenNotFoundException() {
        super("This refresh token is not found. Login again.", HttpStatus.BAD_REQUEST);
    }
}
