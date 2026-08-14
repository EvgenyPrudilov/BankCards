package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(
    description = "Response payload containing security authorization tokens upon successful authentication."
)
public class LoginResponseDto {

    @Schema(
        description = "The JWT access token used to authenticate protected resource requests.",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String accessToken;

    @Schema(
        description = "The JWT refresh token used to safely provision new access tokens without requiring re-authentication.",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String refreshToken;

    @Schema(
        description = "The unique username of the authenticated account holder.",
        example = "bank_customer_7"
    )
    private String username;
}
