package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(
    description = "Response payload containing the paginated collection of retrieved bank cards along with execution metadata."
)
public class GetCardsResponseDto {

    @Schema(
        description = "The list of bank card details matching the requested filter criteria or belonging to the target user."
    )
    private List<GetCardResponseDto> cards;

    @Schema(
        description = "The current page index position within the paginated record set, starting from index 0.",
        example = "0"
    )
    private int pageNumber;

    @Schema(
        description = "The maximum number of card records requested or allowed to be displayed on a single page slice.",
        example = "10"
    )
    private int pageSize;

    @Schema(
        description = "The absolute total count of all bank card records existing in the database that match the criteria.",
        example = "47"
    )
    private long totalElements;

    @Schema(
        description = "The calculated total count of available pages based on total elements and the requested page size.",
        example = "5"
    )
    private int totalPages;
}
