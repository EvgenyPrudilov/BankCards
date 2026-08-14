package com.example.bankcards.exception.security;

import org.springframework.http.HttpStatus;

public final class RefreshTokenExpiredException extends AuthException {
    public RefreshTokenExpiredException() {
        super("This refresh token is expired. Login again.", HttpStatus.BAD_REQUEST);
    }
}
