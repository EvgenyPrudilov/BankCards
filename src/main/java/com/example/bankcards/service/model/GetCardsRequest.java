package com.example.bankcards.service.model;

import com.example.bankcards.service.model.enums.CardStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class GetCardsRequest {
    private UUID userId;
    private CardStatus status;
}
