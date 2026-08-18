package com.example.bankcards.dto.user;

import com.example.bankcards.service.model.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "Response payload containing the details of a specific user account."
)
public class GetUserResponseDto {

    @Schema(
        description = "The unique business identifier (UUID) assigned to the user account.",
        example = "e4219747-f42d-4e32-92a8-e67d9841910e",
        format = "uuid"
    )
    private UUID uuid;

    @Schema(
        description = "The unique authentication handle and login name of the user.",
        example = "super_admin"
    )
    private String userName;

    @Schema(
        description = "The electronic mail address linked directly to the user profile.",
        example = "root@bank.com"
    )
    private String email;

    @Schema(
        description = "The operational state indicating whether the user account is active or disabled.",
        example = "true"
    )
    private boolean enabled;

    @Schema(
        description = "The administrative authority level and security role assigned to the user.",
        example = "ADMIN"
    )
    private UserRole role;
}
