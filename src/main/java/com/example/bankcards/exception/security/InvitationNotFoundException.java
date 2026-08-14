package com.example.bankcards.exception.security;

import org.springframework.http.HttpStatus;

public final class InvitationNotFoundException extends AuthException {
    public InvitationNotFoundException() {
        super("Invalid invitation token.", HttpStatus.BAD_REQUEST);
    }
}
