package com.example.bankcards.dto.card;

import com.example.bankcards.service.model.CardResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(
    description = "Response payload containing the collection of retrieved bank cards for the user."
)
public class GetCardsResponseDto {

    @Schema(
        description = "The list of bank cards matching the requested criteria or belonging to the specified user."
    )
    private List<GetCardsRequestDto> cards;
}
