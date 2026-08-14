package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(
    description = "Request payload used to check the available financial balance of a specific bank card."
)
public class GetCardBalanceRequestDto {

    @Schema(
        description = "The unique identifier of the user who owns the card. This field is optional.",
        example = "123e4567-e89b-12d3-a456-426614174000",
        format = "uuid",
        nullable = true
    )
    private UUID userId;

    @NotNull(message = "Card ID is required.")
    @Schema(
        description = "The unique identifier of the bank card whose balance is being requested.",
        example = "987f6543-e21b-78d3-c987-123456789abc",
        format = "uuid",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID cardId;

    public GetCardBalanceRequestDto setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }
}
