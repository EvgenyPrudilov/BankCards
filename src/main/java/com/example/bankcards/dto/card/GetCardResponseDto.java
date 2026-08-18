package com.example.bankcards.dto.card;

import com.example.bankcards.service.model.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Schema(
    description = "Response payload containing complete information and current operational state of a bank card."
)
public class GetCardResponseDto {

    @Schema(
        description = "The unique business identifier (UUID) assigned to the bank card.",
        example = "e4219747-f42d-4e32-92a8-e67d9841910e"
    )
    private UUID uuid;

    @Schema(
        description = "The masked or encrypted credit card number format for secure visualization.",
        example = "4444 44** **** 4444"
    )
    private String cardNumber;

    @Schema(
        description = "The full name of the card owner as printed on the physical card.",
        example = "JOHN DOE"
    )
    private String holderName;

    @Schema(
        description = "The exact expiration date and time of the card in UTC ISO-8601 format.",
        example = "2029-12-31T23:59:59Z"
    )
    private Instant expiryDate;

    @Schema(
        description = "The current lifecycle and operational status of the bank card.",
        example = "ACTIVE"
    )
    private CardStatus status;

    @Schema(
        description = "The current available financial balance amount remaining on the card.",
        example = "15450.50"
    )
    private BigDecimal balance;
}
