package com.example.bankcards.exception.card;

import org.springframework.http.HttpStatus;

public final class NotEnoughAmountException extends CardManagementException {
    public NotEnoughAmountException() {
        super("Not enough amount.", HttpStatus.BAD_REQUEST);
    }
}