package com.example.bankcards.exception.security;

import org.springframework.http.HttpStatus;

public final class NotRolesInAccessTokenException extends AuthException {
    public NotRolesInAccessTokenException() {
        super("There is no roles in access token.", HttpStatus.BAD_REQUEST);
    }
}
