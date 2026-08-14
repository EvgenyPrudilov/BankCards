package com.example.bankcards.dto.card;

import com.example.bankcards.service.model.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(
    description = "Request payload used to fetch and filter a list of bank cards belonging to a specific user."
)
public class GetCardsRequestDto {

    @NotNull(message = "User ID is required.")
    @Schema(
        description = "The unique identifier of the user whose cards are being requested.",
        example = "123e4567-e89b-12d3-a456-426614174000",
        format = "uuid",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID userId;

    @Schema(
        description = "Optional card status filter. If omitted or null, all cards for the user will be returned.",
        example = "ACTIVE",
        nullable = true
    )
    private CardStatus status;

    public GetCardsRequestDto setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }
}
