package com.example.bankcards.exception.card;

import org.springframework.http.HttpStatus;

public final class SameCardException extends CardManagementException {
    public SameCardException() {
        super("Can't transfer from and into the same card.", HttpStatus.BAD_REQUEST);
    }
}