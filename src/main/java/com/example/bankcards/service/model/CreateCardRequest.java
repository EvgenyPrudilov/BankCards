package com.example.bankcards.service.model;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateCardRequest {
    private UUID userId;
    private String holderName;
}
