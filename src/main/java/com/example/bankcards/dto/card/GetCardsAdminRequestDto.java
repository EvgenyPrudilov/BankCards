package com.example.bankcards.dto.card;

import com.example.bankcards.service.model.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(
    description = "Request payload used to fetch and filter a list of bank cards belonging to a specific user."
)
public class GetCardsAdminRequestDto {

    @Schema(
        description = "The unique identifier of the user who owns the card. This field is optional.",
        example = "123e4567-e89b-12d3-a456-426614174000",
        format = "uuid",
        nullable = true
    )
    private UUID userId;

    @Schema(
        description = "Optional card status filter. If omitted or null, all cards for the user will be returned.",
        example = "ACTIVE",
        nullable = true
    )
    private CardStatus status;

//    public GetCardsAdminRequestDto setUserId(UUID userId) {
//        this.userId = userId;
//        return this;
//    }
}
