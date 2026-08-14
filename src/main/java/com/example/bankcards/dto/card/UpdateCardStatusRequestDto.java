package com.example.bankcards.dto.card;

import com.example.bankcards.service.model.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(
    description = "Request payload used by administrators to update the operational status of a specific bank card."
)
public class UpdateCardStatusRequestDto {

    @NotNull(message = "Card ID is required.")
    @Schema(
        description = "The unique identifier of the card.",
        example = "123e4567-e89b-12d3-a456-426614174000",
        format = "uuid",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID cardId;

    @NotNull(message = "Card status is required.")
    @Schema(
        description = "The new status to be assigned to the bank card.",
        example = "BLOCKED",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CardStatus cardStatus;
}
