package com.example.bankcards.exception.card;

import org.springframework.http.HttpStatus;

public final class NotActiveDebitCardException extends CardManagementException {
    public NotActiveDebitCardException() {
        super("Not active debit card.", HttpStatus.BAD_REQUEST);
    }
}