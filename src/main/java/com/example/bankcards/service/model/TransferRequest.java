package com.example.bankcards.service.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferRequest {
    private UUID userId;
    private UUID fromCardId;
    private UUID toCardId;
    private BigDecimal amount;
}
