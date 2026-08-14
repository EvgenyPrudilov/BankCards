package com.example.bankcards.service.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCardRequest {
    //    private UUID userId;
    private String userName;
    private String holderName;
    private BigDecimal initBalance;
}
