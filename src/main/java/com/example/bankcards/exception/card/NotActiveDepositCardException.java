package com.example.bankcards.exception.card;

import org.springframework.http.HttpStatus;

public final class NotActiveDepositCardException extends CardManagementException {
    public NotActiveDepositCardException() {
        super("Not active deposit card.", HttpStatus.BAD_REQUEST);
    }
}