package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "Response payload with the details of a generated registration administrator token."
)
public class AdminInvitationResponseDto {

    @Schema(
        description = "The email address to which the invitation token must be sent.",
        example = "new_admin@bank.com",
        format = "email"
    )
    private String email;

    @Schema(
        description = "The unique registration token generated for the new administrator.",
        example = "123e4567-e89b-12d3-a456-426614174000",
        format = "uuid"
    )
    private UUID token;

    @Schema(
        description = "The exact timestamp when the invitation token will expire.",
        example = "2026-08-14T16:33:00Z"
    )
    private Instant expiryDate;
}
