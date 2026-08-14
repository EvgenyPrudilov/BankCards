package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "Response payload containing a newly issued access token and an updated refresh token."
)
public class RefreshResponseDto {

    @Schema(
        description = "The new JWT access token to be used for authorized API requests.",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String accessToken;

    @Schema(
        description = "The newly rotated JWT refresh token to prolong the active session.",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String refreshToken;

    @Schema(
        description = "The unique username of the account owner.",
        example = "bank_customer_7"
    )
    private String username;
}
