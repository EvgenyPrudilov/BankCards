package com.example.bankcards.service.model;

import com.example.bankcards.service.model.enums.CardStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCardStatusRequest {
    private UUID cardId;
    private CardStatus cardStatus;
}
