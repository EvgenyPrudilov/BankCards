package com.example.bankcards.service.model;

import com.example.bankcards.service.model.enums.CardStatus;
import lombok.Data;

@Data
public class GetCardsAdminRequest {
    private String userName;
    private CardStatus status;
}
