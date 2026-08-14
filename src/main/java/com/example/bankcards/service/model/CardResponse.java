package com.example.bankcards.service.model;

import com.example.bankcards.service.model.enums.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class CardResponse {
//    private Long id;
    private String cardNumber;
    private String holderName;
    private Instant expiryDate;
    private CardStatus status;
    private BigDecimal balance;
//    private Long userId;
}
