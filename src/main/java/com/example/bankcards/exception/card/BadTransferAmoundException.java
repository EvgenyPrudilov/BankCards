package com.example.bankcards.exception.card;

import org.springframework.http.HttpStatus;

public final class BadTransferAmoundException extends CardManagementException {
    public BadTransferAmoundException() {
        super("A transfer amount must be positive.", HttpStatus.BAD_REQUEST);
    }
}