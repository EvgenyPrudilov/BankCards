package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "Request payload used by other administrators to create invitation token."
)
public class AdminInviteRequestDto {

    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Invalid email format.")
    @Schema(
        description = "The email where the administrative registration link/token will be sent.",
        example = "new_admin@bank.com",
        format = "email",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;
}
