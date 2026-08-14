package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(
    description = "Request payload used to permanently or logically delete a specific bank card from the system."
)
public class DeleteCardRequestDto {

    @NotNull(message = "Card ID is required.")
    @Schema(
        description = "The unique identifier of the bank card that needs to be deleted.",
        example = "987f6543-e21b-78d3-c987-123456789abc",
        format = "uuid",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID cardId;
}
