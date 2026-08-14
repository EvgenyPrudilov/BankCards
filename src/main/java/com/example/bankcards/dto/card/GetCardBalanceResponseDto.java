package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(
    description = "Response payload containing the current financial balance of the requested bank card."
)
public class GetCardBalanceResponseDto {

    @Schema(
        description = "The current available financial balance amount on the card.",
        example = "1250.75"
    )
    private BigDecimal balance;
}
