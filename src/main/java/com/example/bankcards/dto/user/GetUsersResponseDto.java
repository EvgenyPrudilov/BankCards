package com.example.bankcards.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "Response payload containing the paginated collection of system users."
)
public class GetUsersResponseDto {

    @Schema(
        description = "The list of users matching the specified search filtering criteria."
    )
    private List<GetUserResponseDto> users;

    @Schema(
        description = "The current page index position within the record set, starting from 0.",
        example = "0"
    )
    private int pageNumber;

    @Schema(
        description = "The maximum number of user records displayed on a single page slice.",
        example = "10"
    )
    private int pageSize;

    @Schema(
        description = "The absolute total count of user records matching the filter criteria in the database.",
        example = "25"
    )
    private long totalElements;

    @Schema(
        description = "The calculated total count of available pages based on the current page size.",
        example = "3"
    )
    private int totalPages;
}
