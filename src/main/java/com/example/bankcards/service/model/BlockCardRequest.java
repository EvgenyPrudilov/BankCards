package com.example.bankcards.service.model;

import lombok.Data;

import java.util.UUID;

@Data
public class BlockCardRequest {
    private UUID userId;
    private UUID cardId;
}
