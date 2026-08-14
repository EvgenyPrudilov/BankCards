package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(
    description = "Request payload used to execute a financial transfer between two bank cards."
)
public class TransferRequestDto {

    //    @NotNull(message = "User ID is required.")
    @Schema(
        description = "The unique identifier of the user executing the transaction.",
        example = "123e4567-e89b-12d3-a456-426614174000",
        format = "uuid"
//        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID userId;

    @NotNull(message = "Source card ID is required.")
    @Schema(
        description = "The unique identifier of the source bank card from which funds will be debited.",
        example = "987f6543-e21b-78d3-c987-123456789abc",
        format = "uuid",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID fromCardId;

    @NotNull(message = "Destination card ID is required.")
    @Schema(
        description = "The unique identifier of the destination bank card to which funds will be credited.",
        example = "550e8400-e29b-41d4-a716-446655440000",
        format = "uuid",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID toCardId;

    @NotNull(message = "Transfer amount is required.")
    @Positive(message = "Transfer amount must be greater than zero.")
    @Schema(
        description = "The financial amount to be transferred. Must be a positive value.",
        example = "250.50",
        minimum = "0.01",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal amount;

    public TransferRequestDto setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }
}
