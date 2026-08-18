package com.example.bankcards.controller.docs.auth;

import com.example.bankcards.dto.auth.AdminInvitationResponseDto;
import com.example.bankcards.dto.auth.AdminInviteRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Private Authentication Management", description = "Authorized security and administrative invitation operations.")
@SecurityRequirement(name = "BearerAuth")
public interface PrivateControllerDocs {

    @Operation(
        summary = "Log out current user.",
        description = "Invalidates the user session on the server side and clears the secure HttpOnly 'refreshToken' cookie by setting its max-age to 0. Requires a valid JWT token."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully logged out. Refresh token cookie cleared.",
            headers = @Header(name = "Set-Cookie", description = "Clears the HttpOnly 'refreshToken' cookie.", schema = @Schema(type = "string"))
        ),
        @ApiResponse(responseCode = "401", description = "User is not authenticated.", content = @Content)
    })
    ResponseEntity<Void> logout(String userName);

    @Operation(
        summary = "Create an administrative invitation.",
        description = "Generates a unique registration token or invitation link sent to the specified email to allow a new admin user to register. Requires **ROLE_ADMIN** authority."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Administrative invitation successfully created.",
            content = @Content(schema = @Schema(implementation = AdminInvitationResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid invitation request payload or email format.", content = @Content),
        @ApiResponse(responseCode = "401", description = "User is not authenticated.", content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not have the required administrative permissions.", content = @Content)
    })
    ResponseEntity<AdminInvitationResponseDto> inviteNewAdmin(AdminInviteRequestDto requestDto);
}
