package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(
    description = "Request payload used by new consumers to sign up and create a bank user account."
)
public class RegisterRequestDto {

    @NotBlank(message = "Username cannot be empty.")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters long.")
    @Schema(
        description = "Unique username chosen by the user for account identification.",
        example = "bank_customer_7",
        minLength = 4,
        maxLength = 50,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String userName;

    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters long.")
    @Schema(
        description = "Secure password created by the user for account access protection.",
        example = "P@ssword2026",
        minLength = 6,
        maxLength = 100,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Email should be valid.")
    @Schema(
        description = "Primary email address used for notifications and account verification.",
        example = "customer7@example.com",
        format = "email",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;
}
