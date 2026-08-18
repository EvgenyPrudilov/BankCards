package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(
    description = "Request payload used to issue and provision a new bank card for a specific user."
)
public class CreateCardRequestDto {

    @NotNull(message = "User ID is required.")
    @Schema(
        description = "The unique identifier of the user for whom the card is being created.",
        example = "123e4567-e89b-12d3-a456-426614174000",
        format = "uuid",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID userId;

    @NotBlank(message = "Cardholder name cannot be empty.")
    @Schema(
        description = "The full name of the cardholder, typically written in capital Latin letters.",
        example = "JOHN DOE",
        minLength = 1,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String holderName;

    private BigDecimal initBalance;
}
