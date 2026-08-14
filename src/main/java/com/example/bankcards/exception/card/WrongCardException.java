package com.example.bankcards.exception.card;

import org.springframework.http.HttpStatus;

public final class WrongCardException extends CardManagementException {
    public WrongCardException() {
        super("Card doesn't exist or it is not yours.", HttpStatus.BAD_REQUEST);
    }
}