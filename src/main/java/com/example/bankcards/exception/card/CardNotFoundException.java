package com.example.bankcards.exception.card;

import org.springframework.http.HttpStatus;

public final class CardNotFoundException extends CardManagementException {
    public CardNotFoundException() {
        super("Card not found.", HttpStatus.NOT_FOUND);
    }
}
