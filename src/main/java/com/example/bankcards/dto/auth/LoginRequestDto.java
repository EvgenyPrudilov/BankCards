package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(
    description = "Request payload used by users and administrators to authenticate and obtain a JWT token."
)
public class LoginRequestDto {

    @NotBlank(message = "Username is required.")
    @Schema(
        description = "The unique username of the account holder.",
        example = "bank_customer_7",
        minLength = 1,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @NotBlank(message = "Password is required.")
    @Schema(
        description = "The account password.",
        example = "P@ssword2026",
        minLength = 1,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}
