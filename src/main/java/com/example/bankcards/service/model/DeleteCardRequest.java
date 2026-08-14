package com.example.bankcards.service.model;

import lombok.Data;

import java.util.UUID;

@Data
public class DeleteCardRequest {
    private UUID cardId;

}
