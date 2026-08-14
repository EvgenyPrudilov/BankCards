package com.example.bankcards.exception.security;

import org.springframework.http.HttpStatus;

public final class InvitationAlreadyUsedException extends AuthException {
    public InvitationAlreadyUsedException() {
        super("This invitation has already been used.", HttpStatus.BAD_REQUEST);
    }
}
