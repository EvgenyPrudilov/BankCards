package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "Request payload for completing the administrator registration using an invitation token."
)
public class AdminRegisterDto {

    @NotBlank(message = "Username cannot be empty.")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters.")
    @Schema(
        description = "Unique username for the new administrator account.",
        example = "admin_john",
        minLength = 4,
        maxLength = 50,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long.")
    @Schema(
        description = "Secure password for the administrator account.",
        example = "bAnk$ecure2026",
        minLength = 8,
        maxLength = 100,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    @NotNull(message = "Token cannot be empty.")
    @Schema(
        description = "The unique UUID invitation token sent via email.",
        example = "123e4567-e89b-12d3-a456-426614174000",
        format = "uuid",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID token;
}
