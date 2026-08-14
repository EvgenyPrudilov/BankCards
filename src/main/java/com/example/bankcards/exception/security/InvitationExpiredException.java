package com.example.bankcards.exception.security;

import org.springframework.http.HttpStatus;

public final class InvitationExpiredException extends AuthException {
    public InvitationExpiredException() {
        super("This invitation has expired.", HttpStatus.BAD_REQUEST);
    }
}
