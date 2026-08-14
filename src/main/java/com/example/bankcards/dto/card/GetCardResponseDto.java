package com.example.bankcards.dto.card;

import com.example.bankcards.service.model.enums.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class GetCardResponseDto {
    private String cardNumber;
    private String holderName;
    private Instant expiryDate;
    private CardStatus status;
    private BigDecimal balance;
    private UUID uuid;
}
