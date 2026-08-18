package com.example.bankcards.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "Request payload used to search system users based on filtering criteria."
)
public class GetUsersRequestDto {

    @Schema(
        description = "The exact authentication handle or login name of the target user. This field is optional.",
        example = "super_admin",
        nullable = true
    )
    private String userName;

    @Schema(
        description = "The name of the cardholder linked to the bank cards. Supports partial or substring matches. This field is optional.",
        example = "JOHN DOE",
        nullable = true
    )
    private String holderName;
}
