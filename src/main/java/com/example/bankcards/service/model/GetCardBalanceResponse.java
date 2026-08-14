package com.example.bankcards.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class GetCardBalanceResponse {
    private BigDecimal balance;
}
